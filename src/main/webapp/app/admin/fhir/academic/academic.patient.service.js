(function () {
    'use strict';

    angular
        .module('fhirMappingApp')
        .factory('AcademicPatientMapping', AcademicPatientMapping);

    AcademicPatientMapping.$inject = ['$resource'];

    function AcademicPatientMapping ($resource) {
        var service = $resource('api/academicMapping/patient/search', {}, {
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
