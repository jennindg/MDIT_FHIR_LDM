(function() {
    'use strict';

    angular
        .module('fhirMappingApp')
        .controller('CurrentStateObservationController', CurrentStateObservationController);

    CurrentStateObservationController.$inject = ['$scope', 'CurrentStateObservationMapping',  '$state'];

    function CurrentStateObservationController ($scope, CurrentStateObservationMapping,  $state) {
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
            CurrentStateObservationMapping.save(vm.search, onSaveSuccess, onSaveError);
        }
        
        function onSaveSuccess (result) {
            vm.isSaving = false;
            vm.success = "OK";
            
            vm.ldmXml = result.ldmXml;
        	$state.go('currentState-observationSearch', {ldmXml : vm.ldmXml}, { reload: true });

        }

        function onSaveError () {
            vm.isSaving = false;
        }
    }
})();
