(function() {
    'use strict';

    angular
        .module('fhirMappingApp')
        .controller('AcademicPatientController', AcademicPatientController);

    AcademicPatientController.$inject = ['$scope', 'AcademicPatientMapping',  '$state'];

    function AcademicPatientController ($scope, AcademicPatientMapping,  $state) {
        var vm = this;
        
        vm.save = save;
        
        function save () {
        	var startDate = Date.parse(vm.search.startDate);
        	var endDate = Date.parse(vm.search.endDate);        	
        	
        	if(isNaN(startDate) && isNaN(endDate) && endDate < startDate){
        		alert("Start Date and End Date should be valid and Start Date has to be lesser than End Date");
        		return;
        	}
            vm.isSaving = true;
            AcademicPatientMapping.save(vm.search, onSaveSuccess, onSaveError);
        }
        
        function onSaveSuccess (result) {
            vm.isSaving = false;
            vm.success = "OK";
            
            vm.ldmXml = result.ldmXml;
        	$state.go('academic-patientSearch', {ldmXml : vm.ldmXml}, { reload: true });

        }

        function onSaveError () {
            vm.isSaving = false;
        }
    }
})();
