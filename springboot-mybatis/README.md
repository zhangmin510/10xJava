
# 简介

This project is created from a GitLab [Project Template](https://docs.gitlab.com/ce/gitlab-basics/create-project.html)

Additions and changes to the project can be proposed [on the original project](https://gitlab.com/gitlab-org/project-templates/spring)

# 数据库创建

在本地mysql创建springboot数据库，并在springboot下创建表user。sql在resource目录下。


# 运行

```
curl http://localhost:8888/v1/user?userId=1
{"userId":1,"phone":"18506571095"}
```