// src/models/createFileItem.js
export default function createFileItem({
                                           name,
                                           path,
                                           isFolder,
                                           size = 0,
                                           extension = '',
                                           relativePath = '',
                                           modifiedDate = new Date(),
                                           isSelected = false,
                                           permissions = 'rwxr-xr-x',
                                       }) {
    return {
        name,
        path,
        isFolder,
        size,
        extension,
        relativePath,
        modifiedDate,
        isSelected,
        permissions,
    };
}
