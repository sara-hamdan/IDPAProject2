package com.example.idpaproject2;

import com.example.idpaproject2.other.Indexing;
import com.example.idpaproject2.other.MatrixRepresentation;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HelloController {
    @FXML
    public ComboBox vector_selector;
    public ComboBox search_type_selector;
    public CheckBox indexing_checkbox;
    public Button search_btn;
    public TextArea output_text_box;
    public TextField search_box;
    public TextField file_a_input;
    public Button browse_a;
    public Button browse_b;
    public TextField file_b_input;
    public TextField similarity_out;


    public void initialize(){
        file_a_input.setText((new File("src/XMLDocuments/doc5.xml")).getAbsolutePath());
        file_b_input.setText((new File("src/XMLDocuments/doc6.xml")).getAbsolutePath());
    }


    @FXML
    protected void onSearchButtonClicked() {
        System.out.println("search");

        try {
            String vector_mode = (String) this.vector_selector.getValue();
            String search_type = (String) this.search_type_selector.getValue();
            String indexing_checkbox = this.indexing_checkbox.isSelected() ? "Enabled" : "Disabled";

            String query = search_box.getText().toLowerCase();

            Indexing index = new Indexing();

            ArrayList<String> arrayList = index.indexingTerms(query);
            HashMap<String, ArrayList<String>> indexMap = index.getIndexingTable(arrayList);

            ArrayList<String> documents = index.searchInTable(query, indexMap);

            int[] query_TF_Matrix = index.query_TF_Matrix(query, arrayList);
            HashMap<String, Double> ranked = index.rankedSimilarityMap(index.getSimilarity(query_TF_Matrix, index.getDocumentIDFMatrices(documents, arrayList, indexMap)));

            System.out.println(ranked.toString());

            if (ranked.isEmpty()){
                output_text_box.setText("No Relevant Documents Found");
            } else {
                StringBuilder output_text = new StringBuilder();

                int i = 0;
                for (Map.Entry<String, Double> en : ranked.entrySet()) {
                    if (en.getValue() == 0) continue;
                    i++;
                    output_text.append(i).append(". ").append(en.getKey()).append(" (").append(en.getValue()).append(")\n");
                }

                output_text_box.setText(output_text.toString());
            }

        } catch (IOException | XPathExpressionException e){
            System.out.println(e);
        }
    }

    @FXML
    protected void onBrowseA(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Document A");
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            file_a_input.setText(file.getAbsolutePath());
        }
    }

    @FXML
    protected void onBrowseB(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Document B");
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            file_b_input.setText(file.getAbsolutePath());
        }
    }

    @FXML
    protected void onComputeSimilarity(){
        System.out.println("here");

        try {
            MatrixRepresentation mp = new MatrixRepresentation();
            Document document1 = mp.parseXML(file_a_input.getText());
            Document document2 = mp.parseXML(file_b_input.getText());


            Indexing index = new Indexing();


            String query = "I eat apples";

            ArrayList<String> arrayList = index.indexingTerms(query);
            HashMap<String, ArrayList<String>> indexMap = index.getIndexingTable(arrayList);


            ArrayList<String> documents = index.searchInTable(query, indexMap);

            int[] query_TF_Matrix = index.query_TF_Matrix(query, arrayList);
            HashMap<String, Double> ranked = index.rankedSimilarityMap(index.getSimilarity(query_TF_Matrix, index.getDocumentIDFMatrices(documents, arrayList, indexMap)));

            ArrayList<String> indexingNodes1 = mp.getIndexingNodes(document1);
            ArrayList<String> indexingNodes2 = mp.getIndexingNodes(document2);

            ArrayList<String> structureList1 = new ArrayList<>();
            ArrayList<String> structureList2 = new ArrayList<>();
            StringBuilder str1 = new StringBuilder();
            StringBuilder str2 = new StringBuilder();
            mp.getContext(document1.getDocumentElement(), str1, structureList1);
            mp.getContext(document2.getDocumentElement(), str2, structureList2);

            ArrayList<String> mergedIndexingNodes = mp.getMergedIndexingNodes(indexingNodes1, indexingNodes2);
            HashMap<String, Integer> indexMapper = mp.indexLabelling(mp.getUniqueIndexingNodes(mergedIndexingNodes));

            ArrayList<String> mergedStructureList = mp.getMergedStructure(structureList1, structureList2);


            HashMap<String, Integer> structureMapper = mp.structureLabelling(mp.getUniqueIndexingNodes(mergedStructureList));


            mp.IDF_Matrix(document1, document2, mergedIndexingNodes, mergedStructureList);
            mp.TF_Matrix(document1, mergedIndexingNodes, mergedStructureList);

            Double[][] tf_idf_matrix = mp.TF_IDF_Matrix(document1,
                    document2, mp.TF_Matrix(document1, mergedIndexingNodes, mergedStructureList), mp.IDF_Matrix(document1, document2, mergedIndexingNodes, mergedStructureList));


            Double[][] tf_idf_matrix2 = mp.TF_IDF_Matrix(document2,
                    document1, mp.TF_Matrix(document2, mergedIndexingNodes, mergedStructureList), mp.IDF_Matrix(document2, document1, mergedIndexingNodes, mergedStructureList));


            Double sim = mp.computeCosineSim(mp.computeNumCosine(tf_idf_matrix, tf_idf_matrix2), mp.computeDenomCosine(tf_idf_matrix, tf_idf_matrix2));

            similarity_out.setText(sim.toString());

        } catch (IOException | XPathExpressionException e){
            System.out.println(e.toString());
        }

    }
}
