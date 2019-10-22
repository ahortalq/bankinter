// Exported from:        http://kubuntu:5516/#/templates/Folder69f85172321f46a89580c80c09103603-Folder0c3ec44ba3194f6d932dc97005e007c5-Releaseb47781a106914ca0908f583552242016/releasefile
// XL Release version:   9.0.6
// Date created:         Tue Oct 22 19:54:19 CEST 2019

xlr {
  template('Microservicio') {
    folder('Workshop/Bankinter')
    variables {
      stringVariable('sha') {
        required false
        showOnReleaseStart false
        label 'sha'
        value 'sha'
      }
      stringVariable('email') {
        required false
        showOnReleaseStart false
        label 'email'
        value 'josecarlos.lopezayala@gmail.com'
      }
      stringVariable('message') {
        required false
        showOnReleaseStart false
        label 'message'
        value 'Change log'
      }
      stringVariable('build-number') {
        required false
        showOnReleaseStart false
        label 'build-number'
        value '1'
      }
      stringVariable('build-status') {
        required false
        showOnReleaseStart false
        label 'build-status'
        value 'Passed'
      }
      stringVariable('microservice-name') {
        required false
        showOnReleaseStart false
        label 'microservice-name'
        value 'vote'
      }
      stringVariable('ms-version') {
        required false
        showOnReleaseStart false
        label 'ms-version'
        value '1.0.0'
      }
      stringVariable('microservice-application') {
        required false
        showOnReleaseStart false
        label 'microservice-application'
        value 'voting-app'
      }
      stringVariable('issue') {
        required false
        showOnReleaseStart false
        label 'issue'
        value 'CAL-15'
      }
      listVariable('id_phases') {
        required false
        showOnReleaseStart false
        label 'Id de las fases de otros microservicios'
      }
      listVariable('id_phases_pre') {
        required false
        showOnReleaseStart false
        label 'Id de las fases de otros microservicios'
      }
      listVariable('id_phases_pro') {
        required false
        showOnReleaseStart false
        label 'Id de las fases de otros microservicios'
      }
      listVariable('Dependencias Microservicios') {
        required false
        showOnReleaseStart false
        label 'Dependencias Microservicios'
      }
      listBoxVariable('actions') {
        required false
        showOnReleaseStart false
        label 'Continuar?'
        possibleValues 'Continuar con el despliegue al siguiente entorno', 'Realizar Rollback a la version anterior'
        value 'Continuar con el despliegue al siguiente entorno'
      }
      stringVariable('ms-previous-version-dev') {
        required false
        showOnReleaseStart false
        label 'ms-previous-version-dev'
        value '-'
      }
      stringVariable('ms-previous-version-pre') {
        required false
        showOnReleaseStart false
        label 'ms-previous-version-pre'
        value '-'
      }
      stringVariable('ms-previous-version-pro') {
        required false
        showOnReleaseStart false
        label 'ms-previous-version-pro'
        value '-'
      }
      booleanVariable('rollback_pro') {
        required false
        showOnReleaseStart false
        label 'Rollback?'
      }
    }
    description '### 4.1 Permitir al desarrollador generar paquetes de artefactos de distintas tipologías (java, javascript, cobol, css, .pck ..) y coordinar su despliegue.\n' +
                '\n' +
                'XL Release permite, mediante la creación y diseño de templates, modelizar las actividades necesarias para la construcción, publicación, identificación y despliegue de software de distintas tecnologías y a distintos entornos. Gracias a los plugins que proporciona \'out-of-the-box\', a los plugins desarrollados por la comunidad y a la posibilidad de extender y crear plugins personalizados, se integra con todas las herramientas necesarias para modelizar el flujo de trabajo que se desee.\n' +
                'Para poder generar paquetes de artefactos de distintas tipologías se podrían utilizar las integraciones con herramientas como BitBucket, Jenkins, Jira, y los tipos de tareas que permiten ejecutar scripts en remoto y hacer invocaciones API-REST a sistemas externos.\n' +
                'En la siguiente imagen vemos un ejemplo de cómo secuenciar varias actividades relacionadas con BitBucket, Jenkins y Jira.'
    scheduledStartDate Date.parse("yyyy-MM-dd'T'HH:mm:ssZ", '2019-10-16T09:00:00+0200')
    tags 'microservicio', 'jira'
    scriptUsername 'admin'
    scriptUserPassword '{aes:v0}nJ2LT3VwTxFcDmdFJcVvjiC7IlE2D8A1P1tX6zhdzjc='
    phases {
      phase('BUILD') {
        color '#0099CC'
        tasks {
          custom('Obtención de información de repositorio Git') {
            script {
              type 'webhook.JsonWebhook'
              'URL' 'https://api.github.com/repos/jclopeza/${microservice-name}/branches/master'
              result variable('sha')
              result2 variable('email')
              result3 variable('message')
              jsonPathExpression 'commit.sha'
              jsonPathExpression2 'commit.commit.author.email'
              jsonPathExpression3 'commit.commit.message'
            }
          }
          custom('Construcción de la imagen Docker') {
            script {
              type 'jenkins.Build'
              jenkinsServer 'Jenkins Server'
              jobName '${microservice-name}'
              jobParameters 'CHANGELOG=${message}\n' +
                            'EMAIL=${email}'
              buildNumber variable('build-number')
              buildStatus variable('build-status')
            }
          }
          custom('Obtener versión del microservicio construído') {
            script {
              type 'github.GetVersionFromTag'
              server 'GitHub Server'
              organization 
              repositoryName 'vote'
              commitId '${sha}'
              tagVersion variable('ms-version')
            }
          }
          sequentialGroup('Notificación por mensajería') {
            tasks {
              custom('Notificación build correcta para el microservicio ${microservice-name}') {
                script {
                  type 'slack.Notification'
                  server 'Slack Server'
                  channel 'xlr-integration'
                  message 'Microservicio ${microservice-name} en versión ${ms-version} compilado correctamente'
                }
              }
              custom('Notificación Msteams') {
                precondition 'False'
                script {
                  type 'msteams.Notification'
                  
                }
              }
            }
          }
        }
      }
      phase('DEPENDENCIAS') {
        color '#991C71'
        tasks {
          sequentialGroup('Dependencias entre microservicios') {
            precondition 'len(releaseVariables[\'Dependencias Microservicios\']) > 0'
            tasks {
              custom('Obtener dependencias de los microservicios') {
                script {
                  type 'dependencies.GetDependencies'
                  password variable('folder.xlrAdminPassword')
                  phaseName 'DESPLIEGUE DESARROLLO'
                  dependencies variable('Dependencias Microservicios')
                  listIdsDependentPhases variable('id_phases')
                }
              }
              custom('Actualización del gate con las dependencias') {
                script {
                  type 'dependencies.SetDependencies'
                  password variable('folder.xlrAdminPassword')
                  phaseName 'DEPENDENCIAS'
                  taskName 'Esperar a que los microservicios dependientes estén desplegados en DEV'
                  listIdsDependentPhases variable('id_phases')
                }
              }
              gate('Esperar a que los microservicios dependientes estén desplegados en DEV') {
                
              }
            }
          }
        }
      }
      phase('DESPLIEGUE DESARROLLO') {
        color '#08B153'
        tasks {
          custom('Inicio Docker Engine en entorno DEV') {
            script {
              type 'remoteScript.Unix'
              script 'docker-machine ls | grep docker-dev | grep Running || {\n' +
                     '  docker-machine start docker-dev\n' +
                     '  port=`docker-machine inspect --format=\'{{.Driver.SSHPort}}\' docker-dev`\n' +
                     '  ssh -o "StrictHostKeyChecking=no" docker@localhost -p ${port} -i ~/.docker/machine/machines/docker-dev/id_rsa \'sudo -- sh -c "echo 10.0.2.2 lyhsoft-registry >> /etc/hosts"\'\n' +
                     '}'
              scriptIgnoreVariableInterpolation true
              remotePath '/tmp'
              address 'localhost'
              username 'jcla'
              password variable('folder.localHostPassword')
            }
          }
          sequentialGroup('Creación de nueva versión en herramienta de despliegue') {
            tasks {
              custom('Creación de nueva versión en XL Deploy') {
                script {
                  type 'remoteScript.Unix'
                  script '/opt/xebialabs/devops-utilities/yaml-maker.sh -a ${microservice-name} -v ${ms-version}'
                  remotePath '/tmp'
                  address 'localhost'
                  username 'jcla'
                  password variable('folder.localHostPassword')
                }
              }
              custom('Creación de plan de despliegue en CARA') {
                precondition 'False'
                script {
                  type 'nolio.createDeploymentPlan'
                  nolioServer 'CA Release Automation Server'
                }
              }
            }
          }
          sequentialGroup('Despliegue en entorno de Desarrollo') {
            tasks {
              custom('Obtenemos la última versión desplegada del microservicio en el entorno DEV') {
                taskFailureHandlerEnabled true
                taskRecoverOp com.xebialabs.xlrelease.domain.recover.TaskRecoverOp.SKIP_TASK
                script {
                  type 'xld.GetLastVersionDeployed'
                  server 'XL Deploy Server'
                  environmentId 'Environments/application-${microservice-application}-docker/application-${microservice-application}-docker-dev/application-${microservice-application}-docker-dev'
                  applicationName '${microservice-name}-${microservice-application}-docker'
                  applicationId variable('ms-previous-version-dev')
                }
              }
              custom('Despliegue de la versión ${ms-version} del microservicio ${microservice-name} en el entorno DEV') {
                script {
                  type 'xldeploy.Deploy'
                  server 'XL Deploy Server'
                  retryCounter 'currentContinueRetrial':'0','currentPollingTrial':'0'
                  deploymentApplication 'Applications/Applications/application-${microservice-application}-docker/${microservice-name}-${microservice-application}-docker'
                  deploymentVersion '${ms-version}'
                  deploymentPackage 'Applications/Applications/application-${microservice-application}-docker/${microservice-name}-${microservice-application}-docker/${ms-version}'
                  deploymentEnvironment 'Environments/application-${microservice-application}-docker/application-${microservice-application}-docker-dev/application-${microservice-application}-docker-dev'
                }
              }
              custom('Ejecución de despliegue en CARA') {
                precondition 'False'
                script {
                  type 'nolio.runDeployment'
                  nolioServer 'CA Release Automation Server'
                  deploymentResult false
                }
              }
            }
          }
          custom('Actualizar estado ${issue} en Jira - en integración') {
            script {
              type 'jira.UpdateIssue'
              jiraServer 'Jira 7.13.0'
              issueId '${issue}'
              newStatus 'EN INTEGRACIÓN'
              comment 'Desplegado el microservicio en entorno de integración'
            }
          }
        }
      }
      phase('TEST Y VALIDACIÓN') {
        color '#FD8D10'
        tasks {
          notification('Notificación nuevo microservicio desplegado') {
            addresses '${email}'
            subject 'Microservicio ${microservice-name} en versión ${ms-version} desplegado en el entorno de Desarrollo'
            body '### Desplegado nuevo microservicio en el entorno de desarrollo\n' +
                 '\n' +
                 '* `Nombre`: ${microservice-name}\n' +
                 '* `Versión`: ${ms-version}\n' +
                 '* `CommitId`: ${sha}\n' +
                 '* `ChangeLog`: ${message}\n' +
                 '\n' +
                 '### Job de Jenkins encargado de la build\n' +
                 '\n' +
                 '* `Job name`: ${microservice-name}\n' +
                 '* `Nº Build`: ${build-number}\n' +
                 '* `Build status`: ${build-status}'
            replyTo 'lyhsoftcompany@gmail.com'
          }
          userInput('Ejecución de pruebas manuales. Continuar con el despliegue al siguiente entorno.') {
            description 'Please enter the required information below.'
            owner 'admin'
            variables {
              variable 'actions'
            }
          }
          sequentialGroup('Rollback a versión anterior') {
            precondition 'releaseVariables[\'actions\'] == \'Realizar Rollback a la version anterior\''
            tasks {
              custom('Despliegue de la versión anterior del microservicio ${microservice-name} en el entorno DEV') {
                description '### Aplicación desplegada previamente\n' +
                            '\n' +
                            '${ms-previous-version-dev}'
                precondition 'releaseVariables[\'ms-previous-version-dev\'] != \'-\''
                script {
                  type 'xldeploy.Deploy'
                  server 'XL Deploy Server'
                  retryCounter 'currentContinueRetrial':'0','currentPollingTrial':'0'
                  deploymentApplication 
                  deploymentPackage '${ms-previous-version-dev}'
                  deploymentEnvironment 'Environments/application-${microservice-application}-docker/application-${microservice-application}-docker-dev/application-${microservice-application}-docker-dev'
                }
              }
              gate('Rollback realizado, pipeline interrumpido') {
                description '### Se ha interrumpido el pipeline\n' +
                            '\n' +
                            'Se ha realizado un rollback a la versión previa. Debe decidir de forma manual qué hacer.'
                owner 'admin'
              }
            }
          }
          manual('Aprobación pase a PREPRODUCCIÓN') {
            description '### Completar esta tarea para iniciar el despliegue en el entorno de PRE.\n' +
                        '\n' +
                        'Una vez finalizada se iniciará el despliegue automático a PRE'
            owner 'jcla'
            locked true
          }
        }
      }
      phase('DESPLIEGUE PREPRODUCCIÓN') {
        color '#999999'
        tasks {
          custom('Inicio Docker Engine en entorno PRE') {
            script {
              type 'remoteScript.Unix'
              script 'docker-machine ls | grep docker-pre | grep Running || {\n' +
                     '  docker-machine start docker-pre\n' +
                     '  port=`docker-machine inspect --format=\'{{.Driver.SSHPort}}\' docker-pre`\n' +
                     '  ssh -o "StrictHostKeyChecking=no" docker@localhost -p ${port} -i ~/.docker/machine/machines/docker-pre/id_rsa \'sudo -- sh -c "echo 10.0.2.2 lyhsoft-registry >> /etc/hosts"\'\n' +
                     '}'
              scriptIgnoreVariableInterpolation true
              remotePath '/tmp'
              address 'localhost'
              username 'jcla'
              password variable('folder.localHostPassword')
            }
          }
          sequentialGroup('Despliegue en entorno de Preproducción') {
            tasks {
              custom('Obtenemos la última versión desplegada del microservicio en el entorno PRE') {
                taskFailureHandlerEnabled true
                taskRecoverOp com.xebialabs.xlrelease.domain.recover.TaskRecoverOp.SKIP_TASK
                script {
                  type 'xld.GetLastVersionDeployed'
                  server 'XL Deploy Server'
                  environmentId 'Environments/application-${microservice-application}-docker/application-${microservice-application}-docker-pre/application-${microservice-application}-docker-pre'
                  applicationName '${microservice-name}-${microservice-application}-docker'
                  applicationId variable('ms-previous-version-pre')
                }
              }
              custom('Despliegue de la versión ${ms-version} del microservicio ${microservice-name} en el entorno PRE') {
                script {
                  type 'xldeploy.Deploy'
                  server 'XL Deploy Server'
                  retryCounter 'currentContinueRetrial':'0','currentPollingTrial':'0'
                  deploymentApplication 'Applications/Applications/application-${microservice-application}-docker/${microservice-name}-${microservice-application}-docker'
                  deploymentVersion '${ms-version}'
                  deploymentPackage 'Applications/Applications/application-${microservice-application}-docker/${microservice-name}-${microservice-application}-docker/${ms-version}'
                  deploymentEnvironment 'Environments/application-${microservice-application}-docker/application-${microservice-application}-docker-pre/application-${microservice-application}-docker-pre'
                }
              }
              custom('Ejecución de despliegue en CARA') {
                precondition 'False'
                script {
                  type 'nolio.runDeployment'
                  nolioServer 'CA Release Automation Server'
                  deploymentResult false
                }
              }
            }
          }
          custom('Actualizar estado ${issue} en Jira - en preproducción') {
            script {
              type 'jira.UpdateIssue'
              jiraServer 'Jira 7.13.0'
              issueId '${issue}'
              newStatus 'EN PREPRODUCCIÓN'
              comment 'Desplegado el microservicio en entorno de preproducción'
            }
          }
          userInput('Continuar con el despliegue al siguiente entorno') {
            description 'Please enter the required information below.'
            owner 'admin'
            variables {
              variable 'actions'
            }
          }
          sequentialGroup('Rollback a versión anterior') {
            precondition 'releaseVariables[\'actions\'] == \'Realizar Rollback a la version anterior\''
            tasks {
              custom('Despliegue de la versión anterior del microservicio ${microservice-name} en el entorno PRE') {
                description '### Aplicación desplegada previamente\n' +
                            '\n' +
                            '${ms-previous-version-pre}'
                precondition 'releaseVariables[\'ms-previous-version-pre\'] != \'-\''
                script {
                  type 'xldeploy.Deploy'
                  server 'XL Deploy Server'
                  retryCounter 'currentContinueRetrial':'0','currentPollingTrial':'0'
                  deploymentApplication 
                  deploymentPackage '${ms-previous-version-pre}'
                  deploymentEnvironment 'Environments/application-${microservice-application}-docker/application-${microservice-application}-docker-pre/application-${microservice-application}-docker-pre'
                }
              }
              gate('Rollback realizado, pipeline interrumpido') {
                description '### Se ha interrumpido el pipeline\n' +
                            '\n' +
                            'Se ha realizado un rollback a la versión previa. Debe decidir de forma manual qué hacer.'
                owner 'admin'
              }
            }
          }
          parallelGroup('Esperar por autorizaciones remotas') {
            tasks {
              custom('Esperar autorización paso a PRODUCCIÓN') {
                locked true
                script {
                  type 'jira.CheckIssue'
                  jiraServer 'Jira 7.13.0'
                  issueId '${issue}'
                  expectedStatusList 'Autorizado paso a producción'
                }
              }
              custom('Chequeo de estado en Remedy') {
                precondition 'False'
                locked true
                script {
                  type 'remedy.checkStatus'
                  remedyServer 'Remedy Server'
                }
              }
            }
          }
        }
      }
      phase('DESPLIEGUE PRODUCCIÓN') {
        color '#D94C3D'
        tasks {
          custom('Inicio Docker Engine en entorno PRO') {
            script {
              type 'remoteScript.Unix'
              script 'docker-machine ls | grep docker-pro | grep Running || {\n' +
                     '  docker-machine start docker-pro\n' +
                     '  port=`docker-machine inspect --format=\'{{.Driver.SSHPort}}\' docker-pro`\n' +
                     '  ssh -o "StrictHostKeyChecking=no" docker@localhost -p ${port} -i ~/.docker/machine/machines/docker-pro/id_rsa \'sudo -- sh -c "echo 10.0.2.2 lyhsoft-registry >> /etc/hosts"\'\n' +
                     '}'
              scriptIgnoreVariableInterpolation true
              remotePath '/tmp'
              address 'localhost'
              username 'jcla'
              password variable('folder.localHostPassword')
            }
          }
          sequentialGroup('Despliegue en entorno de Producción') {
            tasks {
              custom('Obtenemos la última versión desplegada del microservicio en el entorno PRO') {
                taskFailureHandlerEnabled true
                taskRecoverOp com.xebialabs.xlrelease.domain.recover.TaskRecoverOp.SKIP_TASK
                script {
                  type 'xld.GetLastVersionDeployed'
                  server 'XL Deploy Server'
                  environmentId 'Environments/application-${microservice-application}-docker/application-${microservice-application}-docker-pro/application-${microservice-application}-docker-pro'
                  applicationName '${microservice-name}-${microservice-application}-docker'
                  applicationId variable('ms-previous-version-pro')
                }
              }
              custom('Despliegue de la versión ${ms-version} del microservicio ${microservice-name} en el entorno PRO') {
                script {
                  type 'xldeploy.Deploy'
                  server 'XL Deploy Server'
                  retryCounter 'currentContinueRetrial':'0','currentPollingTrial':'0'
                  deploymentApplication 'Applications/Applications/application-${microservice-application}-docker/${microservice-name}-${microservice-application}-docker'
                  deploymentVersion '${ms-version}'
                  deploymentPackage 'Applications/Applications/application-${microservice-application}-docker/${microservice-name}-${microservice-application}-docker/${ms-version}'
                  deploymentEnvironment 'Environments/application-${microservice-application}-docker/application-${microservice-application}-docker-pro/application-${microservice-application}-docker-pro'
                }
              }
              custom('Ejecución de despliegue en CARA') {
                precondition 'False'
                script {
                  type 'nolio.runDeployment'
                  nolioServer 'CA Release Automation Server'
                  deploymentResult false
                }
              }
            }
          }
          custom('Actualizar estado ${issue} en Jira - en producción') {
            script {
              type 'jira.UpdateIssue'
              jiraServer 'Jira 7.13.0'
              issueId '${issue}'
              newStatus 'EN PRODUCCIÓN'
              comment 'Desplegado el microservicio en entorno de producción'
            }
          }
          userInput('Ejecución de rollback?') {
            description 'Please enter the required information below.'
            owner 'admin'
            variables {
              variable 'rollback_pro'
            }
          }
          sequentialGroup('Rollback a versión anterior') {
            precondition 'releaseVariables[\'rollback_pro\'] == True'
            tasks {
              custom('Despliegue de la versión anterior del microservicio ${microservice-name} en el entorno PRO') {
                description '### Aplicación desplegada previamente\n' +
                            '\n' +
                            '${ms-previous-version-pro}'
                precondition 'releaseVariables[\'ms-previous-version-pro\'] != \'-\''
                script {
                  type 'xldeploy.Deploy'
                  server 'XL Deploy Server'
                  retryCounter 'currentContinueRetrial':'0','currentPollingTrial':'0'
                  deploymentApplication 
                  deploymentPackage '${ms-previous-version-pro}'
                  deploymentEnvironment 'Environments/application-${microservice-application}-docker/application-${microservice-application}-docker-pro/application-${microservice-application}-docker-pro'
                }
              }
              gate('Rollback realizado, pipeline interrumpido') {
                description '### Se ha interrumpido el pipeline\n' +
                            '\n' +
                            'Se ha realizado un rollback a la versión previa. Debe decidir de forma manual qué hacer.'
                owner 'admin'
              }
            }
          }
        }
      }
    }
    extensions {
      dashboard('Dashboard') {
        parentId 'Applications/Folder69f85172321f46a89580c80c09103603/Folder0c3ec44ba3194f6d932dc97005e007c5/Releaseb47781a106914ca0908f583552242016'
        owner 'admin'
        tiles {
          releaseProgressTile('Release progress') {
            row 1
          }
          releaseSummaryTile('Release summary') {
            row 1
          }
          resourceUsageTile('Resource usage') {
            row 3
          }
          timelineTile('Release timeline') {
            row 2
          }
          jiraQueryTile('Preproducción') {
            row 0
            col 1
            jiraServer 'Jira 7.13.0'
            query 'project = "Voting App" AND status in ("En Preproducción", "Autorizado paso a producción", "En Producción")'
          }
          jiraQueryTile('Desarrollo') {
            row 0
            col 0
            jiraServer 'Jira 7.13.0'
            query 'project = "Voting App" AND status in ("En integración", "En Preproducción", "Autorizado paso a producción", "En Producción")'
          }
          jiraQueryTile('JIRA issues') {
            row 0
            col 2
            jiraServer 'Jira 7.13.0'
            query 'project = "Voting App" AND status in ("En Producción")'
          }
          deploymentsDistributionTile('Deployments distribution') {
            row 1
            col 2
            height 1
          }
        }
      }
    }
    
  }
}