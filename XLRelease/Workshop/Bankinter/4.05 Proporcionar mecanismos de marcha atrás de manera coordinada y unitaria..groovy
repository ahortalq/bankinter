// Exported from:        http://kubuntu:5516/#/templates/Folder69f85172321f46a89580c80c09103603-Folder0c3ec44ba3194f6d932dc97005e007c5-Release3eb24cc0cc454ec38b975a3071bc9d1e/releasefile
// XL Release version:   9.0.6
// Date created:         Sat Oct 19 10:18:05 CEST 2019

xlr {
  template('4.05 Proporcionar mecanismos de marcha atrás de manera coordinada y unitaria.') {
    folder('Workshop/Bankinter')
    variables {
      stringVariable('css') {
        required false
        showOnReleaseStart false
        label 'Versión CSS'
        value '1.1'
      }
      stringVariable('javascript') {
        required false
        showOnReleaseStart false
        label 'Versión JAVASCRIPT'
        value '2.1'
      }
      stringVariable('java') {
        required false
        showOnReleaseStart false
        label 'Versión JAVA'
        value '3.1'
      }
      stringVariable('css_actual') {
        required false
        showOnReleaseStart false
        label 'Versión CSS actual'
        value '1.0'
      }
      stringVariable('javascript_actual') {
        required false
        showOnReleaseStart false
        label 'Versión JAVASCRIPT actual'
        value '2.0'
      }
      stringVariable('java_actual') {
        required false
        showOnReleaseStart false
        label 'Versión JAVA actual'
        value '3.0'
      }
      booleanVariable('rollback_css') {
        required false
        showOnReleaseStart false
      }
      booleanVariable('rollback_javascript') {
        required false
        showOnReleaseStart false
      }
      booleanVariable('rollback_java') {
        required false
        showOnReleaseStart false
      }
      listBoxVariable('actions') {
        required false
        showOnReleaseStart false
        label 'Acción'
        possibleValues 'Continuar despliegue', 'Hacer rollback a la version anterior'
        value 'Continuar despliegue'
      }
    }
    description 'This XL Release template shows the **Canary Deployment Pattern**\n' +
                '\n' +
                'The Canary Release deployment pattern is a pattern where a small subset on your production environment is used to test out new features. The environment is split into a \'canary\' part and a \'main\' part. The canary part is typically small and allows for fast deployments. A load balancer is used to direct traffic to all parts of the application, deciding which users go to the new version in the canary section and which users remain on the stable main section.\n' +
                '\n' +
                'For more information, please read [Perform canary deployments](https://docs.xebialabs.com/xl-release/how-to/perform-canary-deployments.html) on our documentation site.'
    scheduledStartDate Date.parse("yyyy-MM-dd'T'HH:mm:ssZ", '2018-01-23T09:00:00+0100')
    scriptUsername 'admin'
    scriptUserPassword '{aes:v0}PBvQdT2bTc48MARteqxCJc+bEchm2gDpOwisuckdCHY='
    phases {
      phase('SELECCIÓN VERSIONES') {
        color '#0099CC'
        tasks {
          userInput('Selección de versiones a desplegar') {
            description '### Seleccione la versión de los componentes a desplegar\n' +
                        '\n' +
                        'En este ejemplo se recoge la `versión actual` de forma manual. Pero esta información habría que obtenerla de la herramienta de despliegue utilizada.'
            variables {
              variable 'css'
              variable 'css_actual'
              variable 'javascript'
              variable 'javascript_actual'
              variable 'java'
              variable 'java_actual'
            }
          }
        }
      }
      phase('DESPLIEGUE CSS') {
        color '#FD8D10'
        tasks {
          sequentialGroup('Componente CSS') {
            owner 'admin'
            tasks {
              script('Despliegue componente CSS ${css}') {
                description 'Use XL Deploy to deploy to the Canary environment.\n' +
                            '\n' +
                            'Configure the XL Deploy server in **Settings > Shared Configuration**.'
                owner 'admin'
                precondition 
                script (['''\
print("Despliegue")
'''])
              }
              script('Registro despliegue CSS Ok') {
                owner 'admin'
                script (['''\
releaseVariables['rollback_css'] = True
'''])
              }
              userInput('Continuar despliegue componente JAVASCRIPT') {
                description 'Please enter the required information below.'
                owner 'admin'
                variables {
                  variable 'actions'
                }
              }
            }
          }
        }
      }
      phase('DESPLIEGUE JAVASCRIPT') {
        color '#08B153'
        tasks {
          sequentialGroup('Componente JAVASCRIPT') {
            owner 'admin'
            precondition 'releaseVariables[\'actions\'] == \'Continuar despliegue\''
            tasks {
              script('Despliegue componente JAVASCRIPT ${javascript}') {
                description 'Use XL Deploy to deploy to the Canary environment.\n' +
                '\n' +
                'Configure the XL Deploy server in **Settings > Shared Configuration**.'
                precondition 
                script (['''\
print("Despliegue")
'''])
              }
              script('Registro despliegue JAVASCRIPT Ok') {
                script (['''\
releaseVariables['rollback_javascript'] = True
'''])
              }
              userInput('Continuar despliegue componente JAVA') {
                description 'Please enter the required information below.'
                variables {
                  variable 'actions'
                }
              }
            }
          }
        }
      }
      phase('DESPLIEGUE JAVA') {
        color '#991C71'
        tasks {
          sequentialGroup('Componente JAVA') {
            owner 'admin'
            precondition 'releaseVariables[\'actions\'] == \'Continuar despliegue\''
            tasks {
              script('Despliegue componente JAVA ${java}') {
                description 'Use XL Deploy to deploy to the Canary environment.\n' +
                '\n' +
                'Configure the XL Deploy server in **Settings > Shared Configuration**.'
                precondition 
                script (['''\
print("Despliegue")
'''])
              }
              script('Registro despliegue JAVA Ok') {
                script (['''\
releaseVariables['rollback_java'] = True
'''])
              }
              userInput('¿Ejecutar Rollback de los cambios aplicados?') {
                description 'Please enter the required information below.'
                variables {
                  variable 'actions'
                }
              }
            }
          }
        }
      }
      phase('ROLLBACK') {
        color '#08b153'
        tasks {
          sequentialGroup('Tareas de Rollback') {
            description 'This block is executed only if tests failed and the Canary environment needs to be rolled back.'
            owner 'admin'
            precondition 'releaseVariables[\'actions\'] == \'Hacer rollback a la version anterior\''
            tasks {
              script('Rollback JAVA a ${java_actual}') {
                description 'Use XL Deploy to rollback the Canary environment to the original version.'
                precondition 'releaseVariables[\'rollback_java\'] == True'
                script (['''\
print("Rollback")
'''])
              }
              script('Rollback JAVASCRIPT a ${javascript_actual}') {
                description 'Use XL Deploy to rollback the Canary environment to the original version.'
                precondition 'releaseVariables[\'rollback_javascript\'] == True'
                script (['''\
print("Rollback")
'''])
              }
              script('Rollback CSS a ${css_actual}') {
                description 'Use XL Deploy to rollback the Canary environment to the original version.'
                precondition 'releaseVariables[\'rollback_css\'] == True'
                script (['''\
print("Rollback")
'''])
              }
            }
          }
        }
      }
    }
    
  }
}