#!/bin/bash

# This script combines docker-compose.yml and Dockerfile into a single file
# for reference purposes. The original files remain unchanged and functional.

# Create the combined file
echo "# Combined Docker Configuration" > docker-combined.txt
echo "" >> docker-combined.txt
echo "This file contains both docker-compose.yml and Dockerfile configurations in one document." >> docker-combined.txt
echo "" >> docker-combined.txt
echo "## Docker Compose Configuration (docker-compose.yml)" >> docker-combined.txt
echo "" >> docker-combined.txt
echo '```yaml' >> docker-combined.txt
cat docker-compose.yml >> docker-combined.txt
echo '```' >> docker-combined.txt
echo "" >> docker-combined.txt
echo "## Dockerfile Configuration (Dockerfile)" >> docker-combined.txt
echo "" >> docker-combined.txt
echo '```dockerfile' >> docker-combined.txt
cat Dockerfile >> docker-combined.txt
echo '```' >> docker-combined.txt
echo "" >> docker-combined.txt
echo "## How to Use" >> docker-combined.txt
echo "" >> docker-combined.txt
echo "1. Both files are already in the correct location and format" >> docker-combined.txt
echo "2. Run 'docker-compose up' to start the services" >> docker-combined.txt

echo "Files have been combined into docker-combined.txt"