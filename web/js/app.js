var app = angular.module("scoutApp", ["firebase"])


    .factory("eventService", ["$firebase",
        function($firebase) {
            // create a reference to the Firebase where we will store our data
            var ref = new Firebase("https://resplendent-heat-6126.firebaseio.com/");
            var sync = $firebase(ref.child("events/"));

            // this uses AngularFire to create the synchronized array
            return sync.$asObject();
        }
    ])
    .factory("rootService", ["$firebase",
        function($firebase) {
            // create a reference to the Firebase where we will store our data
            var ref = new Firebase("https://resplendent-heat-6126.firebaseio.com/");
            var sync = $firebase(ref);

            // this uses AngularFire to create the synchronized array
            return sync.$asArray();
        }
    ])
    .factory("gamesService", ["$firebase",
        function($firebase) {
            // create a reference to the Firebase where we will store our data
            var ref = new Firebase("https://resplendent-heat-6126.firebaseio.com/");
            var sync = $firebase(ref.child("games/"));

            // this uses AngularFire to create the synchronized array
            return sync.$asObject();
        }
    ])
    .controller("ScoutController", ["$scope", "eventService", "rootService", "gamesService",
        function($scope, events, root, games) {
            $scope.events = events;
            $scope.games = games;
            $scope.root = root;
        }
]);

