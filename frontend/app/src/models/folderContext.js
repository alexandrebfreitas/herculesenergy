// src/models/folderContext.js
export default function createFolderContext({ currentPath, folderPath }) {
    return {
        currentPath,
        folderPath,
        // 'items' será definido no componente FolderColumn.vue
    };
}
