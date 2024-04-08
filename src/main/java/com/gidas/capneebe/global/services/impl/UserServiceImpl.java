package com.gidas.capneebe.global.services.impl;

import com.gidas.capneebe.api.models.entities.CaracteristicasFaciales;
import com.gidas.capneebe.api.models.repositories.CaracteristicasFacialesRepository;
import com.gidas.capneebe.global.dtos.responses.StatusResponseDTO;
import com.gidas.capneebe.global.enums.Rol;
import com.gidas.capneebe.global.exceptions.customs.NotFoundException;
import com.gidas.capneebe.global.services.contracts.UserService;
import com.gidas.capneebe.global.utils.reconocer.Reconocer;
import com.gidas.capneebe.security.dtos.request.UserRequestDTO;
import com.gidas.capneebe.security.dtos.response.UserResponseDTO;
import com.gidas.capneebe.security.models.entities.Usuario;
import com.gidas.capneebe.security.models.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final CaracteristicasFacialesRepository caracteristicasFacialesRepository;
    public List<UserResponseDTO> getUsuario() {

        List<UserResponseDTO> Users = new ArrayList<>();
        for (com.gidas.capneebe.security.models.entities.Usuario usuario : userRepository.findAll()) {
            Users.add(UserResponseDTO.builder()
                    .id(usuario.getId())
                    .user(usuario.getUsuario())
                    .firstName(usuario.getNombre())
                    .lastName(usuario.getApellido())
                    .rol(String.valueOf(usuario.getRol()).toUpperCase())
                    .fileNumber(usuario.getLegajo())
                    .build());
        }

        return Users;
    }
    public UserResponseDTO getUsuario(long id) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("usuario not found"));
        return UserResponseDTO.builder()
                .id(usuario.getId())
                .user(usuario.getUsuario())
                .firstName(usuario.getNombre())
                .lastName(usuario.getApellido())
                .rol(String.valueOf(usuario.getRol()).toUpperCase())
                .fileNumber(usuario.getLegajo())
                .build();
    }

    public StatusResponseDTO addUser(UserRequestDTO UserCompany) {



        if (UserCompany.getFirstName() == null || UserCompany.getFirstName().isEmpty() ||
                UserCompany.getLastName() == null || UserCompany.getLastName().isEmpty()) {
            throw new NotFoundException("First name and last name are required");
        }

        validateRole(UserCompany.getRol());

        Usuario usuario = Usuario.builder()
                .usuario(UserCompany.getUserName())
                .nombre(UserCompany.getFirstName())
                .apellido(UserCompany.getLastName())
                .rol(Rol.valueOf(UserCompany.getRol().toUpperCase()))
                .legajo(UserCompany.getFileNumber())
                .build();

        String password = BCrypt.hashpw(UserCompany.getPassword() ,BCrypt.gensalt());
        usuario.setContraseña(password);

        userRepository.save(usuario);

        saveImages(UserCompany.getImages(), usuario.getId(), UserCompany.getFirstName());

        Reconocer.crearBD();

        processDatabaseFile(usuario);

        cleanUp();


        return StatusResponseDTO.builder()
                .status(201)
                .message("usuario added successfully")
                .build();
    }

    private void processDatabaseFile(Usuario usuario) {
        try {
            InputStream input = new FileInputStream("./dataBase.yml");
            InputStreamReader inputStreamReader = new InputStreamReader(input);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // Saltar la primera línea
            bufferedReader.readLine();

            StringBuilder yamlContent = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Reemplazar !!opencv-matrix con una cadena vacía
                line = line.replace("!!opencv-matrix", "");
                yamlContent.append(line).append("\n");
            }

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(yamlContent.toString());

            // Obtener lista de histogramas
            Map<String, Object> opencvLbphfaces = (Map<String, Object>) data.get("opencv_lbphfaces");
            List<Map<String, Object>> histograms = opencvLbphfaces != null ? (List<Map<String, Object>>) opencvLbphfaces.get("histograms") : null;
            if (histograms != null) {
                // Iterar sobre cada histograma (excepto el último)
                for (int i = 0; i < histograms.size(); i++) {
                    Map<String, Object> histogram = histograms.get(i);
                    List<Double> histogramData = (List<Double>) histogram.get("data");

                    // Convertir lista de datos a un formato adecuado (por ejemplo, una cadena)
                    String caracteristica = convertHistogramDataToString(histogramData);

                    // Guardar la característica facial en la base de datos
                    guardarCaracteristicaFacial(caracteristica,usuario);
                }
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private String convertHistogramDataToString(List<Double> histogramData) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Double data : histogramData) {
            stringBuilder.append(data).append(", ");
        }
        return stringBuilder.toString();
    }


    private void guardarCaracteristicaFacial(String caracteristica,Usuario usuario) {
        CaracteristicasFaciales caracteristicasFaciales = CaracteristicasFaciales.builder()
                .caracteristica(caracteristica)
                .usuario(usuario)
                .build();
        caracteristicasFacialesRepository.save(caracteristicasFaciales);
    }

    private void saveImages(List<String> base64Images, Long userId, String firstName) {
        File directory = new File("./rostros");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        int imageCounter = 1;

        for (String base64Image : base64Images) {
            String imageName = userId + "-" + firstName + "-" + imageCounter + ".jpg";

            try {
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                BufferedImage image = ImageIO.read(bis);
                bis.close();

                File output = new File(directory, imageName);
                ImageIO.write(image, "jpg", output);

                imageCounter++;
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public StatusResponseDTO updateUser(long id, UserRequestDTO usuario) {
        Usuario recruiter = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("usuario not found"));

        validateRole(usuario.getRol());

        recruiter.setNombre(usuario.getFirstName());
        recruiter.setApellido(usuario.getLastName());
        recruiter.setContraseña(BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt()));
        recruiter.setRol(Rol.valueOf(usuario.getRol().toUpperCase()));
        recruiter.setLegajo(usuario.getFileNumber());

        userRepository.save(recruiter);
        return StatusResponseDTO.builder()
                .status(200)
                .message("usuario updated successfully")
                .build();
    }

    public StatusResponseDTO removeUser(long id) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("usuario not found"));

        userRepository.deleteById(id);

        return StatusResponseDTO.builder()
                .status(200)
                .message("usuario removed successfully")
                .build();
    }

    private void validateRole(String role) {
        try {
            Rol.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Role not found");
        }
    }
}
