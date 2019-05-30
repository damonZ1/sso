# description
springboot  cas  sso

# we need redis as session storage
# first step start cas server
 ## modify application.yml on yourself set  properties
  ```
  server:
    port: 8090
    compression:
      enabled: true
    connection-timeout: 3000
  spring:
    redis:
        host: 127.0.0.1
        port: 6779
        password: cr2018
    devtools:
        restart:
          enabled: true                       #open
          additional-paths: src/main/java     #listen package
  swagger:
    host: local.dev.com
  cas:
    serverUrl: http://localhost:8090/toLogin
  ```
  ![avatar](https://zdmimage.oss-cn-shenzhen.aliyuncs.com/sso/1.jpg)
# second step start web1
  ## modify application.yml on yourself set the redis properties
  ```
  server:
    port: 8091
    compression:
      enabled: true
    connection-timeout: 3000
  spring:
    redis:
        host: 127.0.0.1
        port: 6779
        password: cr2018
    devtools:
        restart:
          enabled: true                       #open
          additional-paths: src/main/java     #listen package
  swagger:
    host: local.dev.com
  cas:
    serverUrl: http://localhost:8090/toLogin
  ```
 ![avatar](https://zdmimage.oss-cn-shenzhen.aliyuncs.com/sso/2.jpg)
# third step start web2
  ## modify application.yml on yourself set the redis properties
   ```
  server:
    port: 8091
    compression:
      enabled: true
    connection-timeout: 3000
  spring:
    redis:
        host: 127.0.0.1
        port: 6779
        password: cr2018
    devtools:
        restart:
          enabled: true                       #open
          additional-paths: src/main/java     #listen package
  swagger:
    host: local.dev.com
  cas:
    serverUrl: http://localhost:8090/toLogin
  ```
   ![avatar](https://zdmimage.oss-cn-shenzhen.aliyuncs.com/sso/3.jpg)
# now ,we can test 
  ##  open your browser and request localhost:8091/index   it will redirect to localhost:8090/toLogin
   ![avatar](https://zdmimage.oss-cn-shenzhen.aliyuncs.com/sso/4.jpg)
  ##  after authenticing , we redirect to the back url
   ![avatar](https://zdmimage.oss-cn-shenzhen.aliyuncs.com/sso/5.jpg)
  ## now let's request web2
   ![avatar](https://zdmimage.oss-cn-shenzhen.aliyuncs.com/sso/6.jpg)
   ![avatar](https://zdmimage.oss-cn-shenzhen.aliyuncs.com/sso/7.jpg)
  ## let's sign out,and it will redirect to cas page
   ![avatar](https://zdmimage.oss-cn-shenzhen.aliyuncs.com/sso/8.jpg)
   ![avatar](https://zdmimage.oss-cn-shenzhen.aliyuncs.com/sso/10.jpg)
 # complete, thanks for watching!
    

  
