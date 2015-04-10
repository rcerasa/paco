package com.pacoapp.paco.ui;

import java.util.List;

import org.joda.time.DateTime;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.paco.shared.model2.ExperimentDAO;
import com.google.paco.shared.model2.ExperimentGroup;
import com.google.paco.shared.scheduling.ActionScheduleGenerator;
import com.pacoapp.paco.R;
import com.pacoapp.paco.model.Experiment;
import com.pacoapp.paco.model.ExperimentProviderUtil;
import com.pacoapp.paco.utils.IntentExtraHelper;

public class ExperimentGroupPicker extends ActionBarActivity implements ExperimentLoadingActivity {

  public static final String SHOULD_GO_TO_RENDER_NEXT = "should_render_next";
  public static final int FEEDBACK_NEXT = 1;
  public static final int RENDER_NEXT = 2;
  public static final int SCHEDULE_NEXT = 3;
  private Experiment experiment;
  private ExperimentProviderUtil experimentProviderUtil;
  private List<ExperimentGroup> experimentGroups;
  private List<ExperimentGroup> choosableGroups;
  private List<String> choosableGroupNames;
  private int shouldRender;
  private ViewGroup mainLayout;
  private ListView list;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    experimentProviderUtil = new ExperimentProviderUtil(this);
    IntentExtraHelper.loadExperimentInfoFromIntent(this, getIntent(), experimentProviderUtil);
    shouldRender = getIntent().getIntExtra(SHOULD_GO_TO_RENDER_NEXT, RENDER_NEXT);
    if (experiment == null) {
      Toast.makeText(this, R.string.cannot_find_the_experiment_warning, Toast.LENGTH_SHORT).show();
      finish();
    } else {
      mainLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_experiment_group_picker, null);
      setContentView(mainLayout);

      ActionBar actionBar = getSupportActionBar();
      actionBar.setLogo(R.drawable.ic_launcher);
      actionBar.setDisplayUseLogoEnabled(true);
      actionBar.setDisplayShowHomeEnabled(true);
      actionBar.setBackgroundDrawable(new ColorDrawable(0xff4A53B3));
      actionBar.setDisplayHomeAsUpEnabled(true);

      experimentGroups = experiment.getExperimentDAO().getGroups();
      choosableGroupNames = Lists.newArrayList();

      ExperimentDAO experimentDAO = experiment.getExperimentDAO();
      List<ExperimentGroup> groups = experimentDAO.getGroups();
      choosableGroups = Lists.newArrayList();
      for (ExperimentGroup experimentGroup : groups) {
        if (!experimentGroup.getFixedDuration() || (!ActionScheduleGenerator.isOver(new DateTime(), experimentDAO))) {
          choosableGroups.add(experimentGroup);
        }
      }

      for (ExperimentGroup eg : choosableGroups) {
        choosableGroupNames.add(eg.getName());
      }
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                                                              choosableGroupNames);
      list = (ListView) findViewById(R.id.groupList);
      list.setAdapter(adapter);
      list.setOnItemClickListener(new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          onListItemClick(list, view, position, id);

        }
      });
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }


  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  public void setExperiment(Experiment experimentByServerId) {
    this.experiment = experimentByServerId;

  }

  @Override
  public Experiment getExperiment() {
    return experiment;
  }

  @Override
  public void setExperimentGroup(ExperimentGroup groupByName) {
    // no-op for this activity
  }



  protected void onListItemClick(ListView l, View v, int position, long id) {
    ExperimentGroup chosenGroup = choosableGroups.get(position);
    Class clazz = null;
    switch (shouldRender) {
    case RENDER_NEXT:
      if (chosenGroup.getCustomRendering()) {
        clazz = ExperimentExecutorCustomRendering.class;
      } else {
        clazz = ExperimentExecutor.class;
      }
      break;
    case FEEDBACK_NEXT:
      clazz = FeedbackActivity.class;
      break;
    case SCHEDULE_NEXT:
      clazz = ScheduleListActivity.class;
      break;
    }
    Intent experimentIntent = new Intent(this, clazz);
    experimentIntent.putExtra(Experiment.EXPERIMENT_SERVER_ID_EXTRA_KEY, experiment.getExperimentDAO().getId());
    experimentIntent.putExtra(Experiment.EXPERIMENT_GROUP_NAME_EXTRA_KEY, chosenGroup.getName());
    startActivity(experimentIntent);
    finish();

  }



}
