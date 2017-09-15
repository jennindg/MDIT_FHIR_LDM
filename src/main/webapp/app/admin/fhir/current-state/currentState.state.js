(function() {
    'use strict';

    angular
        .module('fhirMappingApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('currentState-observation', {
            parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/fhir/current-state/observationSearch.html',
                    controller: 'CurrentStateObservationController',
                    controllerAs: 'vm'
                }
            }
        })
        .state('currentState-observationSearch', {
        	parent: 'currentState-observation',
            url: '/currentState/observationSearch',
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
                    templateUrl: 'app/admin/fhir/current-state/observationPopup.html',
                    controller: 'CurrentStateDetailController',
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
