package com.atomtech.ecommerce.service;

import com.atomtech.ecommerce.model.Image;
import com.atomtech.ecommerce.model.Product;
import com.atomtech.ecommerce.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletContext;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ImageService implements ServletContextAware {

    private final String IMG_PATH = "/uploads/images/";

    //    la portabilite des chemins
    private String sep = File.separator;
    private final String IMAGES_DIRECTORY = sep+"uploads"+sep+"images"+sep;

    private ServletContext servletContext;

    @Autowired private ImageRepository imageRepository;

    public ResponseEntity<Object> add(@RequestBody Image image) {
        try
        {
            Image image1 = imageRepository.save(image);

            if(image1 == null)
                return ResponseEntity.noContent().build();

            return ResponseEntity.status(201).body(image1);
        }
        catch (Exception e)
        {
            System.out.println("AAS::::) ERROR: "+e.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    public ResponseEntity<Object> upload(long prod_id, MultipartFile[] files)
    {
        try {
            System.out.println("File list:");
            Image image;
            Product product = new Product();
            product.setProd_id(prod_id);
            for (MultipartFile file: files)
            {
                System.out.println("File name: "+file.getOriginalFilename());
                System.out.println("File size: "+file.getSize());
                System.out.println("File type: "+file.getContentType());
                System.out.println("-----------------------------------------");
                image = new Image();
                image.setProduct(product);
                image.setUrl(IMG_PATH+save(file));
                this.add(image);
            }
            return new ResponseEntity<Object>(HttpStatus.OK);
        }
        catch (Exception e)
        {
            System.out.println("Error lors de l'opload");
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
    }


    private String save(MultipartFile file)
    {
        System.out.println("Chemin: "+IMAGES_DIRECTORY+"fileName");
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyymmss");
            String newFileName = simpleDateFormat.format(new Date()) + file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            Path path = Paths.get(this.servletContext.getRealPath(IMAGES_DIRECTORY+newFileName));
            Files.write(path, bytes);
            return newFileName;
        }
        catch (Exception e)
        {
            System.out.println("Error in save");
            return null;
        }
    }


    public List<Image> getAll() {
        return imageRepository.findAll();
    }

    public ResponseEntity<Object> getOne(@PathVariable long img_id) {
        Image image = imageRepository.findById(img_id);
        if (image == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{'Erreur': 'Cette image n'existe pas !'}");


        URI location = ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/rvs/{id}/users")
                .buildAndExpand(image.getImg_id())
                .toUri();

//        Link link = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RvsController.class).getOneUserRvs(user_id)).withRel("rvList");
//        users.add(link);

        return ResponseEntity.created(location).body(image);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
