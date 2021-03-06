angular.module('viaggia.services.conf', [])

.factory('Config', function ($q, $http, $window, $filter, $rootScope, $ionicLoading, $ionicPlatform, $translate) {

    var langPromise = $q.defer();

    $ionicPlatform.ready(function () { // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
      if (typeof navigator.globalization !== "undefined") {
        navigator.globalization.getPreferredLanguage(function (language) {
          $translate.use((language.value).split("-")[0]).then(function (data) {
            console.log("SUCCESS -> " + data);
            langPromise.resolve($translate.use());
          }, function (error) {
            console.log("ERROR -> " + error);
            langPromise.resolve($translate.use());
          });
        }, null);
      }
    });
    var isDarkColor = function (color) {
        if (!color) return true;
        var c = color.substring(1); // strip #
        var rgb = parseInt(c, 16); // convert rrggbb to decimal
        var r = (rgb >> 16) & 0xff; // extract red
        var g = (rgb >> 8) & 0xff; // extract green
        var b = (rgb >> 0) & 0xff; // extract blue

        var luma = (r + g + b) / 3; //0.2126 * r + 0.7152 * g + 0.0722 * b; // per ITU-R BT.709

        return luma < 128;
    };


    $rootScope.textColor = function (color) {
        if (isDarkColor(color)) return '#fff';
        return '#000';
    };

    var weliveLoggingToken = LOGGING_TOKEN;

    var HTTP_CONFIG = {
        timeout: 5000
    };

    var mapJsonConfig = null;
    var ttJsonConfig = null;

    var APP_BUILD = '';

    var COLORS_TRIP = {
        TRAIN: {
            color: '#cd251c',
            listIcon: 'img/ic_mt_train.png',
            icon: 'img/ic_train.png'

        },
        CAR: {
            color: '#757575',
            listIcon: 'img/ic_mt_car.png',
            icon: 'img/ic_car.png'

        },
        BUS: {
            color: '#eb8919',
            listIcon: 'img/ic_mt_bus.png',
            icon: 'img/ic_urbanBus.png'

        },
        TRANSIT: {
            color: '#016a6a',
            listIcon: 'img/ic_mt_funivia.png',
            icon: 'img/ic_funivia.png'

        },
        BUSSUBURBAN: {
            color: '#00588e',
            listIcon: 'img/ic_mt_extraurbano.png',
            icon: 'img/ic_extraurbanBus.png'

        },
        BICYCLE: {
            color: '#096a5e',
            listIcon: 'img/ic_mt_bicycle.png',
            icon: 'img/ic_bike.png'

        },
        WALK: {
            color: '#8cc04c',
            listIcon: 'img/ic_mt_foot.png',
            icon: 'img/ic_walk.png'

        },
        PARKWALK: {
            color: '#8cc04c',
            listIcon: 'img/ic_mt_walk.png',
            icon: 'img/ic_park_walk.png'

        },
        TRIP: {
            color: '#3bbacf'
        },
        PARKING: {
            color: '#164286'
        },
        BIKESHARING: {
            color: '#096a5e'
        }
    };

    return {
        getAgencyId: function() {
          return mapJsonConfig['bikeAgencyId'];
        },
        getWeLiveAppId: function() {
          return mapJsonConfig['weliveAppId'];
        },
        getWeLiveAppName: function() {
          return mapJsonConfig['weliveAppName'];
        },
        init: function () {
            var deferred = $q.defer();
            if (mapJsonConfig != null) deferred.resolve(true);
            else $http.get('data/config.json').success(function (response) {
                mapJsonConfig = response;
                deferred.resolve(true);
            });
            return deferred.promise;
        },
        getHTTPConfig: function () {
            return HTTP_CONFIG;
        },
        getServerURL: function () {
            return mapJsonConfig['serverURL'];
        },
        getMapPosition: function () {
            return {
                lat: mapJsonConfig['center_map'][0],
                long: mapJsonConfig['center_map'][1],
                zoom: mapJsonConfig['zoom_map']
            };
        },
        getColorsTypes: function () {
            return COLORS_TRIP;
        },
        getColorType: function (transportType, agencyId) {
            if (transportType == 'BUS') {
                if (this.getExtraurbanAgencies() && this.getExtraurbanAgencies().indexOf(parseInt(agencyId)) != -1)
                    return COLORS_TRIP['BUSSUBURBAN'];
            }
            return COLORS_TRIP[transportType];
        },
        getAppId: function () {
            return mapJsonConfig["appid"];
        },
        getAppName: function () {
            return mapJsonConfig["appname"];
        },
        getVersion: function () {
            return 'v ' + mapJsonConfig["appversion"] + (APP_BUILD && APP_BUILD != '' ? '<br/>(' + APP_BUILD + ')' : '');
        },
        getLang: function () {
            var browserLanguage = '';
            // works for earlier version of Android (2.3.x)
            var androidLang;
            if ($window.navigator && $window.navigator.userAgent && (androidLang = $window.navigator.userAgent.match(/android.*\W(\w\w)-(\w\w)\W/i))) {
                browserLanguage = androidLang[1];
            } else {
                // works for iOS, Android 4.x and other devices
                browserLanguage = $window.navigator.userLanguage || $window.navigator.language;
            }
            var lang = browserLanguage.substring(0, 2);
            if (lang != 'it' && lang != 'en' && lang != 'de') lang = 'en';
            return lang;
        },
        getLanguage: function () {
          return langPromise.promise;
        },
        loading: function () {
            $ionicLoading.show();
        },
        loaded: function () {
            $ionicLoading.hide();
        },
        getContactLink: function() {
            return mapJsonConfig["contact_link"];
        },
        log: function(type, customAttrs) {

          if (customAttrs == null) customAttrs = {};
          customAttrs.uuid = ionic.Platform.device().uuid;
          customAttrs.appname = mapJsonConfig['weliveAppName'];
          $http.post('https://dev.welive.eu/dev/api/log/'+mapJsonConfig['weliveAppId'],{
            appId: mapJsonConfig['weliveAppId'],
            type: type,
            timestamp: new Date().getTime(),
            custom_attr: customAttrs
          },{headers:{Authorization:'Bearer '+weliveLoggingToken}})
          .then(function(){
          }, function(err) {
            console.log('Logging error: ', err);
          });
        }
    }
})
