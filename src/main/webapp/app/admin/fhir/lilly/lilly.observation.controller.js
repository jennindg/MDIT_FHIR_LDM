(function() {
    'use strict';

    angular
        .module('fhirMappingApp')
        .controller('LillyObservationController', LillyObservationController);

    LillyObservationController.$inject = ['$scope', 'LillyObservationMapping',  '$state'];

    function LillyObservationController ($scope, LillyObservationMapping,  $state) {
        var vm = this;
        
        vm.save = save;
        
        function save () {
        	var startDate = Date.parse(vm.search.observationStartDate);
        	var endDate = Date.parse(vm.search.observationEndDate);        	
        	
        	if(isNaN(startDate) && isNaN(endDate) && endDate < startDate){
        		alert("Start Date and End Date should be valid and Start Date has to be lesser than End Date");
        		return;
        	}
            vm.isSaving = true;
            LillyObservationMapping.save(vm.search, onSaveSuccess, onSaveError);
        }
        
        function onSaveSuccess (result) {
            vm.isSaving = false;
            vm.success = "OK";
            
            vm.ldmXml = result.ldmXml;
        	$state.go('lilly-observationSearch', {ldmXml : vm.ldmXml}, { reload: true });

        }

        function onSaveError () {
            vm.isSaving = false;
        }
    }
})();
