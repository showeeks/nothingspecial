# Readme

这是一个 Spring boot 项目，用来将 doc, docx 文档转换为 pdf 文件。

需要 `/tmp/pdfmake` 文件夹的权限。

## API

`POST /` 上传文件

请求

1. file 需要转换的 doc, docx 文件
2. callback-url 转换完成后需要访问的url

响应

文件名

预期的行为
1. 生成一个转换后的 PDF 文件，含水印。
2. 生成出的文件上传到 oss
3. 上传完成后访问 callback-url

示例请求

```http request
POST http://localhost:8080/
Accept: */*
Cache-Control: no-cache
Content-Type: multipart/form-data; boundary=boundary

--boundary
Content-Disposition: form-data; name="file"; filename="555.docx"

< /Users/john/code/untitled/555.docx

--boundary
Content-Disposition: form-data; name="redirect-url"

http://baidu.com

--boundary--
```

示例响应

```
HTTP/1.1 200 
Content-Type: text/plain;charset=UTF-8
Content-Length: 12
Date: Mon, 22 Jul 2019 03:36:55 GMT

pQR0TuUd.pdf

Response code: 200; Time: 22ms; Content length: 12 bytes
```

## 参数

`applicaiton.properties` 中包含本地目录和oss目录的配置。

## 部署

`/tmp/pdfmake` 必须有读写的权限。

该文件夹须有 `/tmp/pdfmake/generate-dir` 和 `/tmp/pdfmake/upload-dir` 这两个子文件夹。