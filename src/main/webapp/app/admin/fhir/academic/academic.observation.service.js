(function () {
    'use strict';

    angular
        .module('fhirMappingApp')
        .factory('AcademicObservationMapping', AcademicObservationMapping);

    AcademicObservationMapping.$inject = ['$resource'];

    function AcademicObservationMapping ($resource) {
        var service = $resource('api/academicMapping/observation/search', {}, {
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
