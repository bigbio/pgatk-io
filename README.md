pgatk-io
===============
![Java CI with Maven](https://github.com/bigbio/pgatk-io/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# About pgatk-io

The pgatk-io library is a java framework to manipulate mass spectrometry and proteomics file formats. It has an special focus on novel file formats like Apache Spark Parquet and Json file formats for proteomics.

## Support Matrix

This table summarizes the current level of support for each feature across the different file formats. See discussion
below for details on each feature.

| Feature              | MGF                | APL (Maxquant)         | mzXML               | mzML                 | PRIDE Json          |Pep Avro                |
| ---------------------|--------------------|------------------------|---------------------|----------------------|---------------------|------------------------|
| Random Access        | :heavy_check_mark: | :heavy_check_mark:     | :heavy_check_mark:  | :heavy_check_mark:   | :heavy_check_mark:  |                        |
| Fast Iterable Access | :heavy_check_mark: | :white_check_mark:     | :heavy_check_mark:  | :x:                  | :x:                 | :heavy_check_mark:     |
| Gzip Support         | :x:                | :x:                    | :x:                 | :x:                  |:x:                  |                        |
| Numpress Support     | :x:                | :x:                    | :white_check_mark:  | :white_check_mark:   |:x:                  |                        |

### File formats

- MGF - http://www.matrixscience.com/help/data_file_help.html
- mzXML - http://tools.proteomecenter.org/wiki/index.php?title=Formats:mzXML#:~:text=mzXML%20is%20an%20open%20data,foundation%20of%20our%20proteomic%20pipelines
- mzML - https://www.psidev.info/mzML
- ArchiveSpectrum (PRIDE Json) - http://bigbio.xyz/pgatk-io/io/github/bigbio/pgatk/io/pride/ArchiveSpectrum.html
- AnnotatedSpectrum (Pep Avro) - http://bigbio.xyz/pgatk-io/io/github/bigbio/pgatk/io/pride/AnnotatedSpectrum.html

# License

 pgatk-io is licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

# Main Features

- Based on a custom build class to efficiently parse text files line by line all parsers can handle arbitrary large files in minimal memory, allowing easy and efficient processing of peak
list files using the Java programming language.

- For every implementation a **Random** Access and **Iterable** Access Reader is provided.
   - In the **Random** access developers can access to any individual Spectrum using the Identifier of the Spectrum or the index.
   - In the **Iterable** access developers can access one by one each of the spectra with the **next** function

# Getting Help

If you have questions or need additional help, please create an issue in the library repo in github (https://github.com/bigbio/pgatk-io/issues). Please send us your feedback, including error reports, improvement suggestions,
new feature requests and any other things you might want to suggest.

# Similar libraries:

* [ms-data-core-api](https://github.com/PRIDE-Utilities/ms-data-core-api) Perez-Riverol Y., Uszkoreit J., Sanchez A., Ternent T., Del Toro N., Hermjakob H., Vizcaíno J.A., Wang R. ms-data-core-api: an open-source, metadata-oriented library for computational proteomics. Bioinformatics, 2015 Sep 1;31(17):2903-5 [ms-data-core-api](http://bioinformatics.oxfordjournals.org/content/31/17/2903.long)

* [jmzReader](https://github.com/PRIDE-Utilities/jmzReader)  Griss J, Reisinger F, Hermjakob H, Vizcaíno JA. jmzReader: A Java parser library to process and visualize multiple text and XML-based mass spectrometry data formats. Proteomics. 2012 Mar;12(6):795-8. doi: 10.1002/pmic.201100578.


