// Exported from:        http://kubuntu:5516/#/templates/Folder69f85172321f46a89580c80c09103603-Folder0c3ec44ba3194f6d932dc97005e007c5-Releaseb47781a106914ca0908f583552242016/releasefile
// XL Release version:   9.0.6
// Date created:         Tue Oct 22 07:48:46 CEST 2019

xlr {
  template('4.01 Permitir al desarrollador generar paquetes de artefactos de distintas tipologías (java, javascript, cobol, css, .pck ..) y coordinar su despliegue.') {
    folder('Workshop/Bankinter')
    variables {
      stringVariable('sha') {
        required false
        showOnReleaseStart false
        label 'sha'
      }
      stringVariable('email') {
        required false
        showOnReleaseStart false
        label 'email'
      }
      stringVariable('message') {
        required false
        showOnReleaseStart false
        label 'message'
      }
      stringVariable('build-number') {
        required false
        showOnReleaseStart false
        label 'build-number'
      }
      stringVariable('build-status') {
        required false
        showOnReleaseStart false
        label 'build-status'
      }
      stringVariable('microservice-name') {
        required false
        showOnReleaseStart false
        label 'microservice-name'
        value 'vote'
      }
      stringVariable('microservice-version') {
        required false
        showOnReleaseStart false
        label 'microservice-version'
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
      }
    }
    description '### 4.1 Permitir al desarrollador generar paquetes de artefactos de distintas tipologías (java, javascript, cobol, css, .pck ..) y coordinar su despliegue.\n' +
                '\n' +
                'XL Release permite, mediante la creación y diseño de templates, modelizar las actividades necesarias para la construcción, publicación, identificación y despliegue de software de distintas tecnologías y a distintos entornos. Gracias a los plugins que proporciona \'out-of-the-box\', a los plugins desarrollados por la comunidad y a la posibilidad de extender y crear plugins personalizados, se integra con todas las herramientas necesarias para modelizar el flujo de trabajo que se desee.\n' +
                'Para poder generar paquetes de artefactos de distintas tipologías se podrían utilizar las integraciones con herramientas como BitBucket, Jenkins, Jira, y los tipos de tareas que permiten ejecutar scripts en remoto y hacer invocaciones API-REST a sistemas externos.\n' +
                'En la siguiente imagen vemos un ejemplo de cómo secuenciar varias actividades relacionadas con BitBucket, Jenkins y Jira.'
    scheduledStartDate Date.parse("yyyy-MM-dd'T'HH:mm:ssZ", '2019-10-16T09:00:00+0200')
    phases {
      phase('GESTIÓN DEL CÓDIGO FUENTE') {
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
              tagVersion variable('microservice-version')
            }
          }
          sequentialGroup('Notificación por mensajería') {
            tasks {
              custom('Notificación build correcta para el microservicio ${microservice-name}') {
                script {
                  type 'slack.Notification'
                  server 'Slack Server'
                  channel 'xlr-integration'
                  message 'Microservicio ${microservice-name} en versión ${microservice-version} compilado correctamente'
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
                  script '/opt/xebialabs/devops-utilities/yaml-maker.sh -a ${microservice-name} -v ${microservice-version}'
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
          sequentialGroup('Despliegue en entorno de Integración') {
            tasks {
              custom('Despliegue de la versión ${microservice-version} del microservicio ${microservice-name} en el entorno DEV') {
                script {
                  type 'xldeploy.Deploy'
                  server 'XL Deploy Server'
                  retryCounter 'currentContinueRetrial':'0','currentPollingTrial':'0'
                  deploymentApplication 'Applications/Applications/application-${microservice-application}-docker/${microservice-name}-${microservice-application}-docker'
                  deploymentVersion '${microservice-version}'
                  deploymentPackage 'Applications/Applications/application-${microservice-application}-docker/${microservice-name}-${microservice-application}-docker/${microservice-version}'
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
          custom('Actualización estado de la issue ${issue} en Jira') {
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
      phase('CREAR DEPLOYMENT PLAN') {
        color '#FD8D10'
        tasks {
          custom('Lista de versiones disponibles') {
            script {
              type 'artifactory.ArtifactoryQuery'
              
            }
          }
          userInput('Selección de versión a desplegar') {
            description 'Please enter the required information below.'
            variables {
              
            }
          }
          custom('Creación Deployment Plan') {
            script {
              type 'nolio.createDeploymentPlan'
              
            }
          }
          gate('Aprobación pase a DEV') {
            
          }
        }
      }
      phase('DESPLIEGUE A DEV') {
        color '#D94C3D'
        tasks {
          custom('Notificación Teams actualización versión') {
            script {
              type 'msteams.Notification'
              
            }
          }
          custom('Despliegue versión entorno DEV') {
            script {
              type 'nolio.runDeployment'
              deploymentResult false
            }
          }
          manual('Ejecución pruebas manuales') {
            
          }
          gate('Aprobación pase a PRE') {
            
          }
        }
      }
    }
    
  }
}