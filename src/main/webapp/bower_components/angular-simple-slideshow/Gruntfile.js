module.exports = function(grunt){
	
	// Load Grunt tasks
	require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);
	
	grunt.initConfig({
		watch: {
			karma: {
				files: ['angular-simple-slideshow.js', 'test/{,**/}*.js'],
				tasks: ['karma:unit']
			}
		},

		bump: {
			options: {
				commitMessage: 'chore: release v%VERSION%',
				commitFiles: ['package.json', 'bower.json', 'angular-simple-slideshow.min.js'],
				files: ['package.json', 'bower.json'],
				pushTo: 'origin'
			}
		},

		karma: {
			unit: {
				configFile: 'karma.conf.js',
				singleRun: true
			},
		},

		jshint: {
			all: ['Gruntfile.js', 'angular-simple-slideshow.js', 'test/angular-simple-slideshow.spec.js']
		},

		ngmin: {
			dist: {
				files: {
					'angular-simple-slideshow.min.js': ['angular-simple-slideshow.js']
				}
			}
		},

		'npm-contributors': {
			options: {
				commitMessage: 'chore: update contributors'
			}
		},

		uglify: {
			options: {
				mangle: false
			},
			dist: {
				files: {
					'angular-simple-slideshow.min.js': ['angular-simple-slideshow.min.js']
				}
			}
		},
		
		copy: {
			example: {
				src: ['angular-simple-slideshow.js', 'angular-simple-slideshow.css'],
				dest: 'example/',
			}
		}
		
	});
	
	grunt.registerTask('test', [
		'jshint:all',
		'karma:unit'
	]);
	
	grunt.registerTask('build', [
		'jshint:all',
		'ngmin',
		'uglify',
		'copy:example'
	]);
	
	// Default task
	grunt.registerTask('default', ['watch']);
};