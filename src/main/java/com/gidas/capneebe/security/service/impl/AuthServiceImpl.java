package com.gidas.capneebe.security.service.impl;


import com.gidas.capneebe.api.models.entities.CaracteristicasFaciales;
import com.gidas.capneebe.api.models.repositories.CaracteristicasFacialesRepository;
import com.gidas.capneebe.global.exceptions.customs.BadCredentialsException;
import com.gidas.capneebe.global.utils.GenerarYml;
import com.gidas.capneebe.global.utils.reconocer.Reconocer;
import com.gidas.capneebe.security.dtos.response.LoginResponseDTO;
import com.gidas.capneebe.security.jwt.JwtIssuer;
import com.gidas.capneebe.security.models.entities.Usuario;
import com.gidas.capneebe.security.models.repositories.UserRepository;
import com.gidas.capneebe.security.principal.UserPrincipal;
import com.gidas.capneebe.security.principal.UserPrincipalAuthenticationToken;
import com.gidas.capneebe.security.service.contracts.AuthService;
import lombok.RequiredArgsConstructor;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_GRAYSCALE;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CaracteristicasFacialesRepository caracteristicasFacialesRepository;
    private final ResourceLoader resourceLoader;
    private final UserDetailsService userDetailsService;

    public LoginResponseDTO attemptUserPasswordLogin(String user, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtIssuer.generateJWT((UserPrincipal) authentication.getPrincipal());
            return LoginResponseDTO.builder()
                    .accessToken(token)
                    .build();

        }  catch (Exception ex) {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    public LoginResponseDTO attemptFaceLogin(String faceImage) {
        List<CaracteristicasFaciales> caracteristicas = caracteristicasFacialesRepository.findAll();
        GenerarYml.generateYamlFile(caracteristicas);
        saveImages(faceImage);
        opencv_core.Mat img = cargarImagen("imagen.jpg");
        Reconocer reconocedor = new Reconocer();
        reconocedor.loadDatabase();
        long id = reconocedor.reconocer(img);
        cleanUp();

        if (id != -1) {
            Usuario usuario = userRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el ID: " + id));
            Authentication authentication = new UserPrincipalAuthenticationToken(
                    (UserPrincipal) userDetailsService.loadUserByUsername(usuario.getUsuario())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtIssuer.generateJWT((UserPrincipal) authentication.getPrincipal());
            return LoginResponseDTO.builder()
                    .accessToken(token)
                    .build();
        } else {
            throw new BadCredentialsException("Invalid credentials");
        }
    }


    public opencv_core.Mat cargarImagen(String nombreImagen) {
        opencv_core.Mat imagen = null;
        try {
            Resource resource = resourceLoader.getResource("file:rostros/" + nombreImagen);
            File archivoImagen = resource.getFile();
            imagen = opencv_imgcodecs.imread(archivoImagen.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
        }
        return imagen;
    }

    private void saveImages(String base64Image) {
        File directory = new File("./rostros");
        if (!directory.exists()) {
            directory.mkdirs();
        }

         String imageName = "imagen"+ ".jpg";

        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);
            bis.close();

            File output = new File(directory, imageName);
            ImageIO.write(image, "jpg", output);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void cleanUp() {
        File directory = new File("./rostros");
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }

        File databaseFile = new File("./dataBase.yml");
        if (databaseFile.exists()) {
            databaseFile.delete();
        }
    }

}
