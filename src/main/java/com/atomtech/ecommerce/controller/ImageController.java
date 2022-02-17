package com.atomtech.ecommerce.controller;
import com.atomtech.ecommerce.model.Image;
import com.atomtech.ecommerce.model.Product;
import com.atomtech.ecommerce.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;

//    @PostMapping(value = "/images")
//    public ResponseEntity<Object> add(@RequestBody Image image) {
//        return imageService.add(image);
//    }

    @PostMapping(value = "/upload/{prod_id}")
    public ResponseEntity<Object> upload(@PathVariable long prod_id, @RequestParam("files") MultipartFile[] files)
    {
        try {
            return imageService.upload(prod_id, files);
        }
        catch (Exception e)
        {
            System.out.println("Error Uploading");
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping(value = "/images")
    public List<Image> getAll() {
        return imageService.getAll();
    }

    @GetMapping(value = "/images/{img_id}")
    public ResponseEntity<Object> getOne(@PathVariable long img_id) {
        return imageService.getOne(img_id);
    }

}
