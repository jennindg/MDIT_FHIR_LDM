(function () {
    'use strict';

    angular
        .module('fhirMappingApp')
        .factory('LillyObservationMapping', LillyObservationMapping);

    LillyObservationMapping.$inject = ['$resource'];

    function LillyObservationMapping ($resource) {
        var service = $resource('api/lillyMapping/observation/search', {}, {
            'query': {method: 'GET', isArray: false},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'save': {
                method: 'POST',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'remove':{ method:'DELETE'}
        });

        return service;
    }
})();
