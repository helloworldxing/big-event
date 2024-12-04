package com.itheima.controller;

import com.itheima.pojo.Article;
import com.itheima.pojo.PageBean;
import com.itheima.pojo.Result;
import com.itheima.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping
    public Result add(@RequestBody @Validated Article article){
        articleService.add(article);
        return Result.success();

    }
    //获取文章列表
    @GetMapping
    public Result<PageBean<Article>> list(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String state
    ){
        PageBean<Article> pb = articleService.list(pageNum,pageSize,categoryId,state);
        return Result.success(pb);
    }
//获取文章详情
    @GetMapping("/detail")
    public Result<Article> articleInfo(Integer id){
        Article article = articleService.findByArticleId(id);
        if(article == null){
            return Result.error("文章不存在");
        }
        return Result.success(article);
    }
//更新文章
    @PutMapping
    public Result update(@RequestBody @Validated Article article){
        articleService.update(article);
        return Result.success();
    }
//删除文章
    @DeleteMapping
    public Result delete(@RequestBody @Validated Integer id){
        Article article = articleService.findByArticleId(id);
        if(article == null){
            return Result.error("文章不存在");
        }
        articleService.delete(id);
        return Result.success();
    }
}
