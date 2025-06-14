# Git Flow

1. **Create a new branch from main**:
   ```bash
   git checkout main
   git pull origin main
   git checkout -b feature/my-new-feature
   ```

2. **Work on your feature**:
   Make commits and push when ready:
   ```bash
   git add .
   git commit -m "Add new feature"
   git push origin feature/my-new-feature
   ```

3. **Open a Pull Request**:
   Create a Pull Request from `feature/my-new-feature` into `main`.

4. **Merge into main**:
   Once the Pull Request is reviewed and approved, merge it into `main`.

5. **Delete the feature branch**:
   After merging, delete the feature branch as it is no longer needed.

6. **Start a new feature**:
   Branch off from `main` again:
   ```bash
   git checkout main
   git pull origin main
   git checkout -b feature/next-feature
   ```