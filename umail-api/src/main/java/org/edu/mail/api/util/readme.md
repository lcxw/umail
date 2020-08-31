### TokenUtil使用方法
1. 生成token
```
String uid = UUID.randomUUID().toString();
create("123", 100000, uid);
```
2. 验证token
```
verify("eyJraWQiOiI2MWIwYTczYi1mNmY5LTQ5NzMtOTYxNy1mMzNkZTdmYzdhZTAiLCJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE1NTEyNDY5ODEsImlzcyI6ImF1dGgwIiwiZXhwIjoxNTUxMjQ3MDgxLCJpYXQiOjE1NTEyNDY5ODF9.FFNnQeD96bTaUBam26FJa9Vh94UcaLfa0HKg3-C8KP8","123");
```
       