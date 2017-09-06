(function() {
    'use strict';

    angular
        .module('fhirMappingApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state'];

    function HomeController ($scope, Principal, LoginService, $state) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });
        vm.slides = [
						'/content/images/Slide1.GIF',
						'/content/images/Slide2.GIF',
						'/content/images/Slide3.GIF',
						'/content/images/Slide4.GIF',						
						'/content/images/Slide5.GIF',
						'/content/images/Slide6.GIF',
						'/content/images/Slide7.GIF',
						'/content/images/Slide8.GIF',
						'/content/images/Slide9.GIF',
						'/content/images/Slide10.GIF',
						'/content/images/Slide11.GIF',
						'/content/images/Slide12.GIF',	
						'/content/images/Slide13.GIF',	
						'/content/images/Slide14.GIF',
						'/content/images/Slide15.GIF',
						'/content/images/Slide16.GIF',
						'/content/images/Slide17.GIF',	
						'/content/images/Slide18.GIF',	
						'/content/images/Slide19.GIF',	];						

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        function register () {
            $state.go('register');
        }
    }
})();
