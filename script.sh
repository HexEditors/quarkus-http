#!/bin/bash
echo "Cleaning Maven cache..."
rm -rf ~/.m2/repository/com/example/
rm -rf ~/.m2/repository/io/quarkus/

echo "Testing compilation..."
mvn clean compile -DskipTests

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful!"
    echo "Testing package..."
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        echo "✓ Packaging successful!"
        echo "Testing install..."
        mvn clean install -DskipTests
        
        if [ $? -eq 0 ]; then
            echo "✓ Installation successful!"
            echo "Testing verify..."
            mvn clean verify -Dgpg.skip=true

            if [ $? -eq 0 ]; then
                echo "✓ Verify successful!"
                echo "Ready to deploy with: mvn clean deploy"
            else
                echo "✗ Verify failed"
            fi
        else
            echo "✗ Installation failed"
        fi
    else
        echo "✗ Packaging failed"
    fi
else
    echo "✗ Compilation failed"
fi
