@echo off
REM Script to build and run the Flow Log Parser application

REM Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Java is required but not installed. Please install Java.
    exit /b 1
)

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Maven is required but not installed. Please install Maven.
    exit /b 1
)

REM Build the application
echo Building the application...
call mvn clean package

if %ERRORLEVEL% neq 0 (
    echo Build failed. Please check the errors above.
    exit /b 1
)

REM Check if jar was created
if not exist "target\flow-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar" (
    echo JAR file not found. Build may have failed.
    exit /b 1
)

REM Check command line arguments
if "%~3"=="" (
    echo Usage: %0 ^<flow_log_file^> ^<lookup_table_file^> ^<output_file^>

    REM Ask user if they want to generate sample data
    set /p generate_data="Do you want to generate sample data for testing? (y/n): "
    if /i "%generate_data%"=="y" (
        echo Generating sample data...
        java -cp target\flow-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar com.flowparser.util.SampleDataGenerator
        echo Sample data generated: sample_flow_logs.txt and sample_lookup_table.csv

        REM Ask user if they want to run with sample data
        set /p run_sample="Do you want to run the application with the sample data? (y/n): "
        if /i "%run_sample%"=="y" (
            set FLOW_LOG_FILE=sample_flow_logs.txt
            set LOOKUP_TABLE_FILE=sample_lookup_table.csv
            set OUTPUT_FILE=output_stats.csv
        ) else (
            echo Exiting. You can run the application later with your own data.
            exit /b 0
        )
    ) else (
        echo Exiting. Please provide the required files.
        exit /b 0
    )
) else (
    set FLOW_LOG_FILE=%1
    set LOOKUP_TABLE_FILE=%2
    set OUTPUT_FILE=%3

    REM Check if input files exist
    if not exist "%FLOW_LOG_FILE%" (
        echo Flow log file not found: %FLOW_LOG_FILE%
        exit /b 1
    )

    if not exist "%LOOKUP_TABLE_FILE%" (
        echo Lookup table file not found: %LOOKUP_TABLE_FILE%
        exit /b 1
    )
)

REM Run the application
echo Running Flow Log Parser with:
echo   Flow Log File: %FLOW_LOG_FILE%
echo   Lookup Table: %LOOKUP_TABLE_FILE%
echo   Output File: %OUTPUT_FILE%
echo.

java -jar target\flow-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar "%FLOW_LOG_FILE%" "%LOOKUP_TABLE_FILE%" "%OUTPUT_FILE%"

if %ERRORLEVEL% equ 0 (
    echo Processing completed successfully.
    echo Output written to: %OUTPUT_FILE%

    REM Show preview of output file
    echo Preview of output file:
    type %OUTPUT_FILE% | find /v "" /c
    echo Total lines in output file:
    type %OUTPUT_FILE% | find /v "" /n | find "1:"
    type %OUTPUT_FILE% | find /v "" /n | find "2:"
    type %OUTPUT_FILE% | find /v "" /n | find "3:"
    echo ...
) else (
    echo Processing failed. Please check the errors above.
    exit /b 1
)