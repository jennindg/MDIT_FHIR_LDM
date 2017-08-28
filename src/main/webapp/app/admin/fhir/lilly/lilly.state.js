(function() {
    'use strict';

    angular
        .module('fhirMappingApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('lilly-observation', {
            parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/fhir/lilly/observationSearch.html',
                    controller: 'LillyObservationController',
                    controllerAs: 'vm'
                }
            }
        })
        .state('lilly-observationSearch', {
        	parent: 'lilly-observation',
            url: '/lilly/observationSearch',
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
                    templateUrl: 'app/admin/fhir/lilly/observationPopup.html',
                    controller: 'LillyDetailController',
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
        .state('lilly-patient', {
            parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/fhir/lilly/patientSearch.html',
                    controller: 'LillyPatientController',
                    controllerAs: 'vm'
                }
            }
        })
        .state('lilly-patientSearch', {
        	parent: 'lilly-patient',
            url: '/lilly/patientSearch',
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
                    templateUrl: 'app/admin/fhir/lilly/patientPopup.html',
                    controller: 'LillyDetailController',
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
