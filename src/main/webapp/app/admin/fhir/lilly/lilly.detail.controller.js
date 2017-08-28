(function() {
    'use strict';

    angular
        .module('fhirMappingApp')
        .controller('LillyDetailController', LillyDetailController);

    LillyDetailController.$inject = ['$scope', '$uibModalInstance', '$state'];

    function LillyDetailController ($scope, $uibModalInstance,  $state) {
        var vm = this;
        
        vm.clear = clear;
        
        loadXml();
                
        function loadXml () {
        	var unescapedStr = $('<div />').html($state.params.ldmXml).text();
        	vm.ldmXml = vkbeautify.xml(unescapedStr);
        }
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();
