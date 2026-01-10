#!/bin/bash

# Fix indentation issues in modified Java files only
echo "Fixing indentation in modified Java files..."

# Get modified Java files in current branch vs upstream/master
git diff --name-only upstream/master --diff-filter=AM | grep '\.java$' | while read file; do
    if [[ -f "$file" ]]; then
        echo "Fixing: $file"
        
        # Fix 4-space pattern to 4-8-12 pattern
        # 12 spaces -> 8 spaces (if statements)
        sed -i '' 's/^            /        /' "$file"
        
        # 16 spaces -> 12 spaces (if children)
        sed -i '' 's/^                /            /' "$file"
        
        # 20 spaces -> 16 spaces (nested if children)
        sed -i '' 's/^                    /                /' "$file"
        
        # 24 spaces -> 20 spaces (deeply nested)
        sed -i '' 's/^                        /                    /' "$file"
    fi
done

echo "Indentation fix complete!"
