# STAC on Cassandra

The application uses Spring Data Cassandra and DataStax Astra DB to build a REST API for
a [Spatio-Temporal Asset Catalog (STAC)](https://stacspec.org/en) that can scale to hundreds of billions of STAC items.

![img.png](img/img.png)

## Objectives

Run a REST API that connects to DataStax Astra DB

## How this works

We're using Spring Data Cassandra and Datastax Astra DB to build a REST API that stores STAC items and features.

## Data sample that can be used for inserting and retrieving items and features
    Click on end points to perform GET or POST action. Click on "Try it out" button to key in 
    input data
### Item
#### POST /item
   ```
   {
     "stac_version": "1.0.0",
     "stac_extensions": [],
     "type": "Feature",
     "id": "20240725_232000_BILGI02",
     "bbox": [172.91173669923782, 1.3438851951615003, 172.95469614953714, 1.3690476620161975],
     "geometry": {
       "type": "Polygon",
       "coordinates": [[[172.91173669923782, 1.3438851951615003], [172.95469614953714, 1.3438851951615003], 
       [172.95469614953714, 1.3690476620161975], [172.91173669923782, 1.3690476620161975], [172.91173669923782, 1.3438851951615003]]]
     },
     "properties": {
       "title": "Core Item",
       "description": "A sample STAC Item that includes examples of all common metadata",
       "start_datetime": "2020-12-11T22:38:32.125Z",
       "end_datetime": "2020-12-11T22:38:32.327Z",
       "created": "2020-12-12T01:48:13.725Z",
       "updated": "2020-12-12T01:48:13.725Z",
       "platform": "cool_sat1",
       "eo:cloud_cover": 13.1,
       "instruments": ["cool_sensor_v1"],
       "constellation": "ion",
       "mission": "collection 5624",
       "gsd": 0.512
     },
     "collection": "BILGI Test 02"
   }
   ```
   Copy and past above string in  put box and make necessary changes. It would be like below.

![image](img/Item_Request_Body.png)

#### POST /item/images
   For polygon
   ```
   {
      "id": "20230130_010126_SN24_QUICKVIEW_VISUAL_1_1_3_SATL-2KM-55S_308_5798",
      "geometry": {
         "type": "Polygon",
         "crs": {
               "type": "name",
               "properties": {
                  "name": "EPSG:4326"
               }
         },
         "coordinates": [
               [
                  [
                     144.81543,
                     -37.927299
                  ],
                  [
                     144.814896,
                     -37.945313
                  ],
                  [
                     144.83763,
                     -37.945733
                  ],
                  [
                     144.838158,
                     -37.927719
                  ],
                  [
                     144.81543,
                     -37.927299
                  ]
               ]
         ]
      },
      "collection": "quickview-visual",
      "datetime": "2023-01-30T01:01:30.760099+00:00",
      "end_datetime": "2023-01-30T01:01:30.760099+00:00",
      "content": {
         "bbox": [
               144.814896,
               -37.945733,
               144.838158,
               -37.927299
         ],
         "links": [],
         "assets": {
               "cloud": {
                  "href": "s3://satellogic-production-eo-backend-catalog/QUICKVIEW_CLOUD/88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813/20230130_010126_SN24_QUICKVIEW_CLOUD_1_1_3_SATL-2KM-55S_308_5798.tif",
                  "type": "image/tiff; application=geotiff;",
                  "roles": [
                     "cloud"
                  ]
               },
               "preview": {
                  "href": "s3://satellogic-production-eo-backend-catalog/QUICKVIEW_VISUAL/88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813/20230130_010126_SN24_QUICKVIEW_VISUAL_1_1_3_SATL-2KM-55S_308_5798_preview.png",
                  "type": "image/png",
                  "roles": [
                     "overview"
                  ]
               },
               "analytic": {
                  "href": "s3://satellogic-production-eo-backend-catalog/QUICKVIEW_VISUAL/88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813/20230130_010126_SN24_QUICKVIEW_VISUAL_1_1_3_SATL-2KM-55S_308_5798.tif",
                  "type": "image/tiff; application=geotiff;",
                  "roles": [
                     "data"
                  ],
                  "eo:bands": [
                     {
                           "name": "Red",
                           "common_name": "red"
                     },
                     {
                           "name": "Green",
                           "common_name": "green"
                     },
                     {
                           "name": "Blue",
                           "common_name": "blue"
                     },
                     {
                           "name": "NIR",
                           "common_name": "nir"
                     }
                  ]
               },
               "thumbnail": {
                  "href": "s3://satellogic-production-eo-backend-catalog/QUICKVIEW_VISUAL/88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813/20230130_010126_SN24_QUICKVIEW_VISUAL_1_1_3_SATL-2KM-55S_308_5798_thumbnail.png",
                  "type": "image/png",
                  "roles": [
                     "thumbnail"
                  ]
               }
         },
         "properties": {
               "gsd": 1.24,
               "datetime": "2023-01-30T01:01:30.760099328+00:00",
               "platform": "newsat24",
               "grid:code": "SATL-2KM-55S_308_5798",
               "proj:epsg": 32755,
               "proj:shape": [
                  1613,
                  1612
               ],
               "eo:cloud_cover": 73.0,
               "proj:transform": [
                  1.24,
                  0.0,
                  307999.88,
                  0.0,
                  -1.24,
                  5800000.8,
                  0.0,
                  0.0,
                  1.0
               ],
               "view:off_nadir": 23.2014,
               "satl:outcome_id": "88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813",
               "satl:ground_lock": false,
               "satl:valid_pixel": 0.153,
               "view:sun_azimuth": 51.5664,
               "satl:product_name": "QUICKVIEW_VISUAL",
               "satl:shadow_cover": 1.9,
               "satl:ba_nir_radial": 1.146,
               "satl:ba_nir_xshift": -1.139,
               "satl:ba_nir_yshift": -0.123,
               "satl:ba_red_radial": 2.964,
               "satl:ba_red_xshift": -2.964,
               "satl:ba_red_yshift": -0.005,
               "view:sun_elevation": 61.6236,
               "satl:ba_blue_radial": 0.82,
               "satl:ba_blue_xshift": 0.019,
               "satl:ba_blue_yshift": 0.82,
               "satl:transaction_id": "rr-to-stac-vh9c7",
               "satl:ba_nir_response": 0.601,
               "satl:ba_red_response": 0.645,
               "satl:product_version": "1.1.3",
               "satl:ba_blue_response": 0.492,
               "satl:geoaccuracy_red_radial": 105.189,
               "satl:geoaccuracy_red_xshift": -71.555,
               "satl:geoaccuracy_red_yshift": 77.101,
               "satl:geoaccuracy_blue_radial": 171.945,
               "satl:geoaccuracy_blue_xshift": 171.944,
               "satl:geoaccuracy_blue_yshift": -0.237,
               "satl:geoaccuracy_green_radial": 1.821,
               "satl:geoaccuracy_green_xshift": 0.364,
               "satl:geoaccuracy_green_yshift": 1.784,
               "satl:geoaccuracy_red_response": 0.134,
               "satl:geoaccuracy_blue_response": -0.045,
               "satl:geoaccuracy_green_response": 0.174
         },
         "stac_extensions": [
               "https://stac-extensions.github.io/projection/v1.1.0/schema.json",
               "https://stac-extensions.github.io/eo/v1.1.0/schema.json",
               "https://stac-extensions.github.io/view/v1.0.0/schema.json",
               "https://stac-extensions.github.io/grid/v1.1.0/schema.json"
         ]
      },
      "private": null
   }
   ```
   
   For point
   ```
   {
      "id": "20230130_010126_SN24_QUICKVIEW_VISUAL_1_1_3_SATL-2KM-55S_308_5798",
      "geometry": {
         "type": "Point",
         "crs": {
               "type": "name",
               "properties": {
                  "name": "EPSG:4326"
               }
         },
         "coordinates": [
                     144.81543,
                     -37.927299
         ]
      },
      "collection": "quickview-visual",
      "datetime": "2023-01-30T01:01:30.760099+00:00",
      "end_datetime": "2023-01-30T01:01:30.760099+00:00",
      "content": {
         "bbox": [
               144.814896,
               -37.945733,
               144.838158,
               -37.927299
         ],
         "links": [],
         "assets": {
               "cloud": {
                  "href": "s3://satellogic-production-eo-backend-catalog/QUICKVIEW_CLOUD/88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813/20230130_010126_SN24_QUICKVIEW_CLOUD_1_1_3_SATL-2KM-55S_308_5798.tif",
                  "type": "image/tiff; application=geotiff;",
                  "roles": [
                     "cloud"
                  ]
               },
               "preview": {
                  "href": "s3://satellogic-production-eo-backend-catalog/QUICKVIEW_VISUAL/88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813/20230130_010126_SN24_QUICKVIEW_VISUAL_1_1_3_SATL-2KM-55S_308_5798_preview.png",
                  "type": "image/png",
                  "roles": [
                     "overview"
                  ]
               },
               "analytic": {
                  "href": "s3://satellogic-production-eo-backend-catalog/QUICKVIEW_VISUAL/88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813/20230130_010126_SN24_QUICKVIEW_VISUAL_1_1_3_SATL-2KM-55S_308_5798.tif",
                  "type": "image/tiff; application=geotiff;",
                  "roles": [
                     "data"
                  ],
                  "eo:bands": [
                     {
                           "name": "Red",
                           "common_name": "red"
                     },
                     {
                           "name": "Green",
                           "common_name": "green"
                     },
                     {
                           "name": "Blue",
                           "common_name": "blue"
                     },
                     {
                           "name": "NIR",
                           "common_name": "nir"
                     }
                  ]
               },
               "thumbnail": {
                  "href": "s3://satellogic-production-eo-backend-catalog/QUICKVIEW_VISUAL/88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813/20230130_010126_SN24_QUICKVIEW_VISUAL_1_1_3_SATL-2KM-55S_308_5798_thumbnail.png",
                  "type": "image/png",
                  "roles": [
                     "thumbnail"
                  ]
               }
         },
         "properties": {
               "gsd": 1.24,
               "datetime": "2023-01-30T01:01:30.760099328+00:00",
               "platform": "newsat24",
               "grid:code": "SATL-2KM-55S_308_5798",
               "proj:epsg": 32755,
               "proj:shape": [
                  1613,
                  1612
               ],
               "eo:cloud_cover": 73.0,
               "proj:transform": [
                  1.24,
                  0.0,
                  307999.88,
                  0.0,
                  -1.24,
                  5800000.8,
                  0.0,
                  0.0,
                  1.0
               ],
               "view:off_nadir": 23.2014,
               "satl:outcome_id": "88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813",
               "satl:ground_lock": false,
               "satl:valid_pixel": 0.153,
               "view:sun_azimuth": 51.5664,
               "satl:product_name": "QUICKVIEW_VISUAL",
               "satl:shadow_cover": 1.9,
               "satl:ba_nir_radial": 1.146,
               "satl:ba_nir_xshift": -1.139,
               "satl:ba_nir_yshift": -0.123,
               "satl:ba_red_radial": 2.964,
               "satl:ba_red_xshift": -2.964,
               "satl:ba_red_yshift": -0.005,
               "view:sun_elevation": 61.6236,
               "satl:ba_blue_radial": 0.82,
               "satl:ba_blue_xshift": 0.019,
               "satl:ba_blue_yshift": 0.82,
               "satl:transaction_id": "rr-to-stac-vh9c7",
               "satl:ba_nir_response": 0.601,
               "satl:ba_red_response": 0.645,
               "satl:product_version": "1.1.3",
               "satl:ba_blue_response": 0.492,
               "satl:geoaccuracy_red_radial": 105.189,
               "satl:geoaccuracy_red_xshift": -71.555,
               "satl:geoaccuracy_red_yshift": 77.101,
               "satl:geoaccuracy_blue_radial": 171.945,
               "satl:geoaccuracy_blue_xshift": 171.944,
               "satl:geoaccuracy_blue_yshift": -0.237,
               "satl:geoaccuracy_green_radial": 1.821,
               "satl:geoaccuracy_green_xshift": 0.364,
               "satl:geoaccuracy_green_yshift": 1.784,
               "satl:geoaccuracy_red_response": 0.134,
               "satl:geoaccuracy_blue_response": -0.045,
               "satl:geoaccuracy_green_response": 0.174
         },
         "stac_extensions": [
               "https://stac-extensions.github.io/projection/v1.1.0/schema.json",
               "https://stac-extensions.github.io/eo/v1.1.0/schema.json",
               "https://stac-extensions.github.io/view/v1.0.0/schema.json",
               "https://stac-extensions.github.io/grid/v1.1.0/schema.json"
         ]
      },
      "private": null
   }
   ```

#### GET /item

![image](img/Item_get.png)

### Item-Collection
#### POST /item-collection
   ```
{
    "id": "20230130_010126_SN24_QUICKVIEW_VISUAL_1_1_3_SATL-2KM-55S_308_5798",
    "geometry": {
        "type": "Polygon",
        "crs": {
            "type": "name",
            "properties": {"name": "EPSG:4326"}
        },
        "coordinates": [[[144.81543, -37.927299], [144.814896, -37.945313], [144.83763, -37.945733],[144.838158,-37.927719],
                [144.81543, -37.927299]]]
    },
    "collection": "quickview-visual",
    "datetime": "2023-01-30T01:01:30.760099+00:00",
    "end_datetime": "2023-01-30T01:01:30.760099+00:00",
    "content": {
        "bbox": [144.814896, -37.945733, 144.838158, -37.927299],
        "links": [],
        "assets": {
            "cloud": {
                "href": "s3://satellogic-production-eo-backend-catalog/QUICKVIEW_CLOUD/88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813/20230130_010126_SN24_QUICKVIEW_CLOUD_1_1_3_SATL-2KM-55S_308_5798.tif",
                "type": "image/tiff; application=geotiff;",
                "roles": [
                    "cloud"
                ]
            },
            "preview": {
                "href": "s3://satellogic-production-eo-backend-catalog/QUICKVIEW_VISUAL/88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813/20230130_010126_SN24_QUICKVIEW_VISUAL_1_1_3_SATL-2KM-55S_308_5798_preview.png",
                "type": "image/png",
                "roles": [
                    "overview"
                ]
            },
            "analytic": {
                "href": "s3://satellogic-production-eo-backend-catalog/QUICKVIEW_VISUAL/88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813/20230130_010126_SN24_QUICKVIEW_VISUAL_1_1_3_SATL-2KM-55S_308_5798.tif",
                "type": "image/tiff; application=geotiff;",
                "roles": [
                    "data"
                ],
                "eo:bands": [
                    {
                        "name": "Red",
                        "common_name": "red"
                    },
                    {
                        "name": "Green",
                        "common_name": "green"
                    },
                    {
                        "name": "Blue",
                        "common_name": "blue"
                    },
                    {
                        "name": "NIR",
                        "common_name": "nir"
                    }
                ]
            },
            "thumbnail": {
                "href": "s3://satellogic-production-eo-backend-catalog/QUICKVIEW_VISUAL/88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813/20230130_010126_SN24_QUICKVIEW_VISUAL_1_1_3_SATL-2KM-55S_308_5798_thumbnail.png",
                "type": "image/png",
                "roles": [
                    "thumbnail"
                ]
            }
        },
        "properties": {
            "gsd": 1.24,
            "datetime": "2023-01-30T01:01:30.760099328+00:00",
            "platform": "newsat24",
            "grid:code": "SATL-2KM-55S_308_5798",
            "proj:epsg": 32755,
            "proj:shape": [
                1613,
                1612
            ],
            "eo:cloud_cover": 73.0,
            "proj:transform": [
                1.24,
                0.0,
                307999.88,
                0.0,
                -1.24,
                5800000.8,
                0.0,
                0.0,
                1.0
            ],
            "view:off_nadir": 23.2014,
            "satl:outcome_id": "88d2d7ad-1ee6-40cf-b68e-52f5ff828ecb--112813",
            "satl:ground_lock": false,
            "satl:valid_pixel": 0.153,
            "view:sun_azimuth": 51.5664,
            "satl:product_name": "QUICKVIEW_VISUAL",
            "satl:shadow_cover": 1.9,
            "satl:ba_nir_radial": 1.146,
            "satl:ba_nir_xshift": -1.139,
            "satl:ba_nir_yshift": -0.123,
            "satl:ba_red_radial": 2.964,
            "satl:ba_red_xshift": -2.964,
            "satl:ba_red_yshift": -0.005,
            "view:sun_elevation": 61.6236,
            "satl:ba_blue_radial": 0.82,
            "satl:ba_blue_xshift": 0.019,
            "satl:ba_blue_yshift": 0.82,
            "satl:transaction_id": "rr-to-stac-vh9c7",
            "satl:ba_nir_response": 0.601,
            "satl:ba_red_response": 0.645,
            "satl:product_version": "1.1.3",
            "satl:ba_blue_response": 0.492,
            "satl:geoaccuracy_red_radial": 105.189,
            "satl:geoaccuracy_red_xshift": -71.555,
            "satl:geoaccuracy_red_yshift": 77.101,
            "satl:geoaccuracy_blue_radial": 171.945,
            "satl:geoaccuracy_blue_xshift": 171.944,
            "satl:geoaccuracy_blue_yshift": -0.237,
            "satl:geoaccuracy_green_radial": 1.821,
            "satl:geoaccuracy_green_xshift": 0.364,
            "satl:geoaccuracy_green_yshift": 1.784,
            "satl:geoaccuracy_red_response": 0.134,
            "satl:geoaccuracy_blue_response": -0.045,
            "satl:geoaccuracy_green_response": 0.174
        },
        "stac_extensions": [
            "https://stac-extensions.github.io/projection/v1.1.0/schema.json",
            "https://stac-extensions.github.io/eo/v1.1.0/schema.json",
            "https://stac-extensions.github.io/view/v1.0.0/schema.json",
            "https://stac-extensions.github.io/grid/v1.1.0/schema.json"
        ]
    },
    "private": null
}
   ```


## Getting Started

To build and play with this app, follow the build instructions that are located
here: [https://github.com/Anant/cass-stac](#prerequisites)

### Prerequisites

1. Download the Astra CLI:
   ```curl -Ls "https://dtsx.io/get-astra-cli" | bash ```
2. \[Optional] Download CQLSH to be able to query the DB from the command line

   Install the Python version you need for CQLSH: Python2.7+ or Python 3.6+

   ```
   curl -O -L https://downloads.datastax.com/enterprise/cqlsh-astra.tar.gz
   curl -O  https://downloads.datastax.com/enterprise/cqlsh-astra-20221114-bin.tar.gz
   tar xvfz cqlsh-astra.tar.gz
   tar xvfz cqlsh-astra-20221114-bin.tar.gz
   mv cqlsh-astra ~/cqlsh-astra
   export CQLSH_PATH=~/cqlsh-astra/
   export PATH=${CQLSH_PATH}/bin:${PATH} >> ~/.bashrc
   ```


3. [Setup Datastax Astra DB](#DataStax-Astra)

### DataStax Astra

1. Create a [DataStax Astra account](https://dtsx.io/38HWu73) if you don't already have one:
   ![image](img/01.png)

2. On the home page. Locate the button **`Create Database`** both vector and non-vector support the Cassandra CQL
   ![image](img/02.png)

3. Populate the fields and click create database
   ![image](img/03.png)

4. After your database is provisioned, we need to generate an Application Token for our App. Go to the `Settings` tab in
   the database home screen.
   ![image](img/04.png)

5. Generate a token by clicking `Generate Token` and give it a name.
   ![image](img/05.png)
6. After you have your Application Token, head to the database connect screen and select the driver connection that we
   need. Go ahead and download the `Secure Bundle` for the driver.

   The bundle might be downloaded as well using the astra-cli:
   ```
   astra db download-scb <db_name> -f secure-connect-db-name.zip
   ```
   ![image](img/06.png)

7. Create keyspace.

## 🚀 Getting Started:

### Running on Gitpod

1. Click the 'Open in Gitpod' link:
   [![Open in IDE](https://gitpod.io/button/open-in-gitpod.svg)](https://anant-cassstac-cngps3ptqta.ws-eu115.gitpod.io/)

2. Once your Gitpod workspace has loaded, you'll be asked to paste your DB credentials in the Gitpod terminal at the
   bottom of the screen:
   ![img.png](img/07.png)

3. When the app is finished building, click the 'Open Browser' button on the bottom right of the screen:

   ![img.png](img/08.png)
4. You've successfully built a Spring Data Datastax application!
   ![img.png](img/09.png)

<!--- ENDEXCLUDE --->

### Local Setup

*Make sure you've completed the [prerequisites](#prerequisites) before starting this step*

1. Prepare Java and maven to be the Right Version
   ```
   sdk list java
   sdk install java 17.0.12-amzn
   sdk list maven
   sdk install maven 3.9.8
   ```
2. Update the right config [properties files](src/main/resources/application.properties) with the Database credentials
   ```
   datastax.astra.username=token
   datastax.astra.password=## FILL IN PASSWORD FROM Datastax's UI ##
   datastax.astra.keyspace=<keyspace>
   datastax.astra.secure-connect-bundle=<secure-bundle-file.zip>
   ```
3. Compile and Run
   ```
   mvn compile
   mvn package -DskipTests=true
   mvn spring-boot:run
   ```   
4. Hit http://localhost:8080/swagger-ui/index.html#/ and start using the API
5. \[Optional] Connect to the DB from the command line

```
$CQLSH_PATH/bin/cqlsh \
-u token \
-p <password> \
-b ./secure-connect-cass5-stac.zip
```
## 🚀 Getting Started:

### Running on Docker
#### Prerequisites
    Docker installed on your system.
    Basic knowledge of Docker and command-line interface.
#### Files in the Repository
    Dockerfile: Contains the instructions to build the Docker image.
    dockerignore: Lists files and directories to be ignored by Docker.
    dockersetup.sh: Shell script to set up the Docker environment and download SCB from Astra.
### 1. Clone the Repository
   Clone the repository containing these files to your local machine.

   ![img.png](img/Git_clone.png)

### 2. Build the Docker Image
   Use the Dockerfile to build the Docker image. Run the following command in the directory containing the Dockerfile.
   Command would run dockersetup.sh internally and setup environmental variables
   ```
      sudo docker build -t container_name \
      --build-arg ASTRA_DB_USERNAME=token \
      --build-arg ASTRA_DB_KEYSPACE=keyspace_name \
      --build-arg ASTRA_DB_ID=astra_database_id \
      --build-arg DATASTAX_ASTRA_PASSWORD=Astra_application_password \
      --build-arg DATASTAX_ASTRA_SCB_NAME=SCB.zip \
      .
   ```
![img.png](img/Docker_Build.png)

### 3. On successful completion, out put of build command would be like below

![img.png](img/Docker_build_command_output.png)

### 4. Access the Application
   Once the container is running, you can access the application using the URL and port specified while building container.
   Below is the command to start docker container.
   ```
      docker run -p port_no:port_no \
      -e SERVER_PORT=8080 \
      -e ASTRA_DB_USERNAME=token \
      -e ASTRA_DB_KEYSPACE=keyspace_name \
      -e ASTRA_DB_ID=astra_database_id \
      -e DATASTAX_ASTRA_PASSWORD=Astra_application_password \
      -e DATASTAX_ASTRA_SCB_NAME=SCB.zip \
      container_name
   ```

![img.png](img/Docker_run_command.png)

#### 5. If docker run command is successful and application is started, output would be like below

![img.png](img/Docker_run_command_output.png)

#### 6. Use browser to access application using bleow url
      ```
      http://localhost:8080/swagger-ui/index.html#/
      UI wold be like below
      ```
   ![img.png](img/09.png)

#### 7. \[Optional] Connect to the DB from the command line
      ```
      $CQLSH_PATH/bin/cqlsh \
      -u token \
      -p <password> \
      -b ./SCB.zip
      ```
