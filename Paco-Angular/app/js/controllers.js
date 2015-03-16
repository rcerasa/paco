var app = angular.module('pacoControllers', []);

app.controller('ExperimentCtrl', ['$scope', '$http', '$routeParams', 'config', 
  function($scope, $http, $routeParams, config) {

  $scope.experimentIdx = parseInt($routeParams.experimentIdx);
  $scope.selectedIndex = 1;
  $scope.loaded = false;

  //$http.get('/experiments?id=5629499534213120').success(function(data) {
  $http.get('js/experiment.json').success(function(data) {
    $scope.experiment = data[$scope.experimentIdx];
    $scope.loaded = true;
    $scope.$broadcast('experimentChange');
  });

  $scope.addGroup = function() {
    $scope.experiment.groups.push(config.groupTemplate);
  }

  $scope.addInput = function(inputs, event, expandFn) {
    inputs.push({});
    expandFn(true);
    event.stopPropagation();
  }

  $scope.addScheduleTrigger = function(triggers, event, expandFn) {
    triggers.push(config.scheduleTriggerTemplate);
    expandFn(true);
    event.stopPropagation();
  }

  $scope.addEventTrigger = function(triggers, event, expandFn) {
    triggers.push(config.eventTriggerTemplate);
    expandFn(true);
    event.stopPropagation();
  }

  $scope.remove = function(arr, idx) {
    arr.splice(idx, 1);
  };

  $scope.save = function() {
    var json = JSON.stringify($scope.experiment);
    var result = $http.post('/save', json);
    result.success(function(data, status, headers, config) {
      alert('success');
    });
    result.error(function(data, status, headers, config) {
      alert( 'failure message: ' + JSON.stringify({data: data}));
    });
  }

}]);




app.controller('InputsCtrl', ['$scope', 'config', function($scope, config) {

  $scope.responseTypes = config.responseTypes;

  $scope.addChoice = function(input) {
    if (input.listChoices === undefined) {
      input.listChoices = [];
    }
    input.listChoices.push('');
  }
}]);


app.controller('ExpandCtrl', ['$scope', function($scope) {

  $scope.expand = false;

  $scope.toggleExpand = function(flag) {
    if (flag === undefined) {
      $scope.expand = !$scope.expand;
    } else {
      $scope.expand = flag;
    }
  }

  $scope.$on('experimentChange', function(event, args) {
    $scope.expand = true;
  });
}]);


app.controller('TriggerCtrl', ['$scope', '$mdDialog', 'config', 
  function($scope, $mdDialog, config) {

  $scope.scheduleTypes = config.scheduleTypes;

  $scope.getType = function(idx) {
    return $scope.scheduleTypes[idx];
  };

  $scope.showSchedule = function(event, schedule) {
    $mdDialog.show({
      targetEvent: event,
      templateUrl: 'partials/schedule.html',
      locals: {
        schedule: schedule
      },
      controller: 'ScheduleCtrl'
    });
  };

  $scope.showAction = function(event, action) {
    $mdDialog.show({
      targetEvent: event,
      templateUrl: 'partials/action.html',
      locals: {
        action: action
      },
      controller: 'ActionCtrl'
    });
  };

}]);


app.controller('ActionCtrl', ['$scope', '$mdDialog', 'config', 'action', 
  function($scope, $mdDialog, config, action) {

  $scope.action = action;
  $scope.actionTypes = config.actionTypes;

  $scope.hide = function() {
    $mdDialog.hide();
  };

}]);


app.controller('ScheduleCtrl', ['$scope', '$mdDialog', 'config', 'schedule', 
  function($scope, $mdDialog, config, schedule) {

  $scope.schedule = schedule;
  $scope.scheduleTypes = config.scheduleTypes;
  $scope.weeksOfMonth = config.weeksOfMonth;
  $scope.esmPeriods = config.esmPeriods;
  $scope.repeatRates = range(1, 30);
  $scope.daysOfMonth = range(1, 31);

  function range(start, end) {
    var arr = [];
    for (var i = start; i <= end; i++) {
      arr.push(i);
    }
    return arr;
  }

  $scope.addTime = function(times, idx) {
    times.splice(idx + 1, 0, {
      'fixedTimeMillisFromMidnight': 0
    });
  };

  $scope.remove = function(arr, idx) {
    arr.splice(idx, 1);
  };

  $scope.hide = function() {
    $mdDialog.hide();
  };

  $scope.$watchCollection('schedule.days', function(days) {
    var sum = 0;
    if (days) {
      for (var i = 0; i < 7; i++) {
        if ($scope.schedule.days[i]) {
          sum += Math.pow(2, i);
        }
      }
      $scope.schedule.weekDaysScheduled = sum;
    }
  });

  $scope.$watch('schedule.scheduleType', function(times) {
    if (times) {
      $scope.schedule.signalTimes = [{}];
    }
  });
}]);


app.controller('SummaryCtrl', ['$scope', 'config', function($scope, config) {

  $scope.getActionSummary = function() {
    if ($scope.action.actionCode !== undefined) {
      return config.actionTypes[$scope.action.actionCode];
    } else {
      return 'Undefined';
    }
  };

  $scope.getScheduleSummary = function() {
    var sched = $scope.schedule;
    var str = '';

    //ispiro:using === for these comparisons breaks on schedule edit
    if (sched.scheduleType == 0) {
      if (sched.repeatRate == 1) {
        str += 'Every day';
      } else {
        str += 'Every ' + sched.repeatRate + ' days'
      }
    } else if (sched.scheduleType == 1) {
      str += 'Every weekday';
    } else if (sched.scheduleType == 2) {
      if (sched.repeatRate == 1) {
        str += 'Every week';
      } else {
        str += 'Every ' + sched.repeatRate + ' weeks'
      }
    } else if (sched.scheduleType == 3) {
      if (sched.repeatRate == 1) {
        str += 'Every month';
      } else {
        str += 'Every ' + sched.repeatRate + ' months'
      }
    } else if (sched.scheduleType == 4) {
      str += config.scheduleTypes[4] + ', ' + sched.esmFrequency + ' time';
      if (sched.esmFrequency > 1) {
        str += 's per day';
      } else {
        str += ' per day';
      }
      //TODO(ispiro):Use period when model supports it
    } else if (sched.scheduleType == 5) {
      str = 'Self report only';
    } else {
      str = 'Undefined';
    }

    if (sched.scheduleType >= 0 && sched.scheduleType <= 3) {
      str += ', ' + sched.signalTimes.length;
      if (sched.signalTimes.length == 1) {
        str += ' time each';
      } else {
        str += ' times each';
      }
    }

    return str;
  };
}]);
