(function() {
    'use strict';

    angular
        .module('fhirMappingApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('academic-observation', {
            parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/fhir/academic/observationSearch.html',
                    controller: 'AcademicObservationController',
                    controllerAs: 'vm'
                }
            }
        })
        .state('academic-observationSearch', {
        	parent: 'academic-observation',
            url: '/academic/observationSearch',
            data: {
                authorities: []
            },
            params: {
                ldmXml: {
                	squash: true
                }
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/admin/fhir/academic/observationPopup.html',
                    controller: 'AcademicDetailController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                            };
                        }
                    }
                });
            }]
        })
        .state('academic-patient', {
            parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/fhir/academic/patientSearch.html',
                    controller: 'AcademicPatientController',
                    controllerAs: 'vm'
                }
            }
        })
        .state('academic-patientSearch', {
        	parent: 'academic-patient',
            url: '/academic/patientSearch',
            data: {
                authorities: []
            },
            params: {
                ldmXml: {
                	squash: true
                }
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/admin/fhir/academic/patientPopup.html',
                    controller: 'AcademicDetailController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                            };
                        }
                    }
                });
            }]
        });
    }
    
})();
