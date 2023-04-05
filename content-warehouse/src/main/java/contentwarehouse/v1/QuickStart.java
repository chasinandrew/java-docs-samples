/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package contentwarehouse.v1;

// [START contentwarehouse_quickstart]

import com.google.cloud.contentwarehouse.v1.DocumentSchemaServiceClient;
import com.google.cloud.contentwarehouse.v1.DocumentSchemaServiceSettings;
import com.google.cloud.contentwarehouse.v1.DocumentServiceClient;
import com.google.cloud.contentwarehouse.v1.DocumentServiceSettings;
import com.google.cloud.contentwarehouse.v1.LocationName;
import com.google.cloud.contentwarehouse.v1.Property;
import com.google.cloud.contentwarehouse.v1.CreateDocumentRequest;
import com.google.cloud.contentwarehouse.v1.CreateDocumentResponse;
import com.google.cloud.contentwarehouse.v1.CreateDocumentSchemaRequest;
import com.google.cloud.contentwarehouse.v1.Document;
import com.google.cloud.contentwarehouse.v1.DocumentSchema;
import com.google.cloud.contentwarehouse.v1.PropertyDefinition;
import com.google.cloud.contentwarehouse.v1.RequestMetadata;
import com.google.cloud.contentwarehouse.v1.TextArray;
import com.google.cloud.contentwarehouse.v1.TextTypeOptions;
import com.google.cloud.contentwarehouse.v1.UserInfo;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class QuickStart {
  
  public static void main(String[] args)
      throws IOException, InterruptedException, ExecutionException, TimeoutException {
    // TODO(developer): Replace these variables before running the sample.
    String projectNumber = "your-project-number";
    String location = "your-region"; // Format is "us" or "eu".
    String userId = "your-user-id"; // Format is user:<user-id>
    quickStart(projectNumber, location, userId);
  }

  public static void quickStart(String projectId, String location, String userId)
      throws IOException, InterruptedException, ExecutionException, TimeoutException {

    String endpoint = String.format("%s-contentwarehouse.googleapis.com:443", location);
    DocumentSchemaServiceSettings documentSchemaServiceSettings = 
         DocumentSchemaServiceSettings.newBuilder().setEndpoint(endpoint).build(); 
  
  // Create a Schema Service client
  try(DocumentSchemaServiceClient documentSchemaServiceClient = DocumentSchemaServiceClient.create(documentSchemaServiceSettings)){

    /*  The full resource name of the location, e.g.:
    projects/{project_number}/locations/{location} */
    String parent = LocationName.format(projectId, location);

    // Create Document Schema with Text Type Property
    DocumentSchema documentSchema = DocumentSchema.newBuilder()
      .setDisplayName("My Test Schema")
      .setDescription("My Test Schema's Description")
      .addPropertyDefinitions(
        PropertyDefinition.newBuilder()
        .setName("stock_symbol")
        .setDisplayName("Searchable text")
        .setIsSearchable(true)
        .setTextTypeOptions(TextTypeOptions.newBuilder().build())
        .build()).build();

    // Define Document Schema request
    CreateDocumentSchemaRequest createDocumentSchemaRequest = CreateDocumentSchemaRequest.newBuilder()
      .setParent(parent)
      .setDocumentSchema(documentSchema).build();

    // Create Document Schema
    DocumentSchema documentSchemaResponse = documentSchemaServiceClient.createDocumentSchema(createDocumentSchemaRequest); 
    

    // Create Document Service Client Settings
    DocumentServiceSettings documentServiceSettings = 
          DocumentServiceSettings.newBuilder().setEndpoint(endpoint).build();
    TextArray textArray = TextArray.newBuilder().addValues("GOOG").build();

    // Create Document Service Client and Document with relevant properties 
      try(DocumentServiceClient documentServiceClient = DocumentServiceClient.create(documentServiceSettings)){
        Document document = Document.newBuilder()
          .setDisplayName("My Test Document")
          .setDocumentSchemaName(documentSchemaResponse.getName())
          .setPlainText("This is a sample of a document's text.")
          .addProperties(
            Property.newBuilder()
            .setName(documentSchema.getPropertyDefinitions(0).getName())
            .setTextValues(textArray)).build();

        // Define Request Metadata for enforcing access control
        RequestMetadata requestMetadata = RequestMetadata.newBuilder()
        .setUserInfo(
          UserInfo.newBuilder()
            .setId(userId).build()).build();
        
        // Define Create Document Request 
        CreateDocumentRequest createDocumentRequest = CreateDocumentRequest.newBuilder()
          .setParent(parent)
          .setDocument(document)
          .setRequestMetadata(requestMetadata)
          .build();
      
        // Create Document
        CreateDocumentResponse createDocumentResponse = documentServiceClient.createDocument(createDocumentRequest);
        
        System.out.println(createDocumentResponse.getDocument().getName());
        System.out.println(documentSchemaResponse.getName());
      }
    }
  }
}
// [END contentwarehouse_quickstart]
