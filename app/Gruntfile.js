module.exports = function(grunt) {

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    uglify: {
      options: {
        banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyymmddHHMM") %> */\n'
      },
      build: {
        src: 'src/<%= pkg.name %>.js',
        dest: 'build/<%= pkg.name %>.min.js'
      }
    },
    // Metadata.
        meta: {
            basePath: '../',
            srcPath: 'assets/sass/',
            deployPath: 'assets/css/'
        },
    // Task configuration.
        sass: {
            dist: {
                files: {
                    '<%= meta.deployPath %>style.css': '<%= meta.srcPath %>style.scss'
                },
                options: {
                    sourcemap: 'true'
                }
            }
        },
        watch: {
            scripts: {
                files: [
                    '<%= meta.srcPath %>/**/*.scss'
                ],
                tasks: ['sass']
            }
        },
      // Task clean  
         clean: {
            dist: {
              files: [{
                dot: true,
                src: [
                  '.tmp',
                  '<%= meta.deployPath %>/*',
                  '!<%= meta.deployPath %>/.git*'
                ]
              }]
            },
            // server: '.tmp'
          },
        
        cssmin: {
            dist: {
              files: {
                '<%= meta.deployPath %>style.min.<%= grunt.template.today("yyyymmddHHMM") %>.css': '<%= meta.deployPath %>style.css',
              
              }
            }
          }



  });

  

  // 加载包含 "uglify" 任务的插件。

  // grunt.loadNpmTasks('grunt-contrib-uglify');
  // grunt.loadNpmTasks('grunt-contrib-sass');
  // grunt.loadNpmTasks('grunt-contrib-watch');


  // load all grunt tasks
  require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);

  // 默认被执行的任务列表。
  grunt.registerTask('default', ['uglify','clean','sass','cssmin']);

};
