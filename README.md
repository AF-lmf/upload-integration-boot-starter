# Upload Integration

> 整合本地、市面常见OOS以及Fast Dfs的springboot starter


### 使用方式

- 打包项目安装到本地maven仓库或上传私服，pom引入依赖
    ```xml
        <dependency>
            <groupId>com.lmf</groupId>
            <artifactId>upload-integration-boot-starter</artifactId>
            <version>1.0.1</version>
        </dependency>
    ```

- yml或properties文件中添加配置
  >说明：组件目前支持 Local、Fast-DFS、腾讯云COS、阿里云OSS、华为云OBS的上传与删除文件功能，  
  > 部分支持配置图片压缩水印等处理策略，引入依赖不做配置默认创建Local文件处理器，文件存储本地。  
    ```yaml
        #腾讯云配置示例
        attachment:
            type: tencentcos
            tencent-cos:
              cos-tencent-domain: xxxxx-xxxxx-xxxxxx.cos.ap-shanghai.myqcloud.com
              cos-tencent-secret-id: xxxxxxxxxxxxxxxxxxxxxxxxxxxx
              cos-tencent-secret-key: xxxxxxxxxxxxxxxxxxxxxxxxxxx
              cos-tencent-region: ap-shanghai
              cos-tencent-bucket-name: xxxx-xxxxx-1305086754
              cos-tencent-source: xxxxx
    ```
    ```yaml
        #Fast-DFS配置示例
        attachment:
            type: fastdfs
            fast-dfs:
              fast-dfs-resource-url: http://10.10.10.10:8088
              fast-dfs-tracker-server: 10.10.10.10:22122
    ```

- 注入FileHandlers调用upload方法上传文件
    ```java
        @Resource
        private FileHandlers fileHandlers;
        
        @PostMapping
        public ResultResponse<UploadResult> save(MultipartFile file) throws Exception {
            UploadResult upload = fileHandlers.upload(file);
            return new ResultResponse<>(upload);
        }  
    ```