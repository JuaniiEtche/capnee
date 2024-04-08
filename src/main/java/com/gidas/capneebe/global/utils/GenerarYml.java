package com.gidas.capneebe.global.utils;
import com.gidas.capneebe.api.models.entities.CaracteristicasFaciales;
import com.gidas.capneebe.api.models.repositories.CaracteristicasFacialesRepository;
import jdk.swing.interop.SwingInterOpUtils;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
public class GenerarYml {

    public static void generateYamlFile(List<CaracteristicasFaciales> caracteristicasFacialesList) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);

        StringBuilder yamlContent = new StringBuilder();
        yamlContent.append("%YAML:1.0\n");
        yamlContent.append("---\n");
        yamlContent.append("opencv_lbphfaces:\n");
        yamlContent.append("   threshold: 1.7976931348623157e+308\n");
        yamlContent.append("   radius: 1\n");
        yamlContent.append("   neighbors: 8\n");
        yamlContent.append("   grid_x: 8\n");
        yamlContent.append("   grid_y: 8\n");
        yamlContent.append("   histograms:\n");

        for (CaracteristicasFaciales caracteristicasFaciales : caracteristicasFacialesList) {
            List<String> numeros = List.of(caracteristicasFaciales.getCaracteristica().split(","));
            yamlContent.append("      - !!opencv-matrix\n");
            yamlContent.append("         rows: 1\n");
            yamlContent.append("         cols: ").append(numeros.size()-1).append("\n");
            yamlContent.append("         dt: f\n");
            yamlContent.append("         data: [ ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numeros.size()-1; i++) {
                sb.append(numeros.get(i));
                if (i != numeros.size() - 1) {
                    sb.append(",");
                }
            }
            sb.deleteCharAt(sb.length() - 1); // Elimina la última coma
            String numerosString = sb.toString();
            yamlContent.append(numerosString);

            yamlContent.append(" ]\n");
        }


        yamlContent.append("   labels: !!opencv-matrix\n");
        yamlContent.append("      rows: ").append(caracteristicasFacialesList.size()).append("\n");
        yamlContent.append("      cols: 1\n");
        yamlContent.append("      dt: i\n");
        yamlContent.append("      data: [ ");
        for (int i = 0; i < caracteristicasFacialesList.size(); i++) {
            CaracteristicasFaciales caracteristicasFaciales = caracteristicasFacialesList.get(i);
            yamlContent.append(caracteristicasFaciales.getUsuario().getId());
            if (i < caracteristicasFacialesList.size() - 1) {
                yamlContent.append(", ");
            }
        }
        yamlContent.append(" ]\n");
        yamlContent.append("   labelsInfo:\n");
        yamlContent.append("      []\n");


        // Escribir el contenido en un archivo
        try (FileWriter writer = new FileWriter("dataBase.yml")) {
            writer.write(yamlContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
