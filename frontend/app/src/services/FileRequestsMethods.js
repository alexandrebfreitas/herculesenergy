// src/services/FileRequestsMethods.js

import axios from 'axios';

/**
 * Configuração base para o axios.
 * Ajuste o baseURL conforme a configuração do seu back-end.
 */
const apiClient = axios.create({
    baseURL: '/api/file', // Base URL para as requisições de arquivo
    headers: {
        'Content-Type': 'application/json',
    },
});

/**
 * Lista os arquivos e pastas em um caminho específico.
 * @param {String} path - Caminho relativo para listar os arquivos/pastas.
 * @returns {Promise<Array>} - Lista de objetos FileInfoDTO.
 */
export const listFiles = (path = '') => {
    return apiClient.get('/list', { params: { path } });
};

/**
 * Cria uma nova pasta em um caminho específico.
 * @param {String} folderName - Nome da nova pasta.
 * @param {String} path - Caminho relativo onde a pasta será criada.
 * @returns {Promise<String>} - Mensagem de sucesso ou erro.
 */
export const createFolder = (folderName, path = '') => {
    const formData = new FormData();
    formData.append('folderName', folderName);
    formData.append('path', path);

    return apiClient.post('/create-folder', formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
};

/**
 * Renomeia um arquivo ou pasta.
 * @param {String} oldPath - Caminho atual do arquivo/pasta.
 * @param {String} newPath - Novo caminho após renomear.
 * @returns {Promise<void>}
 */
export const renameFile = (oldPath, newPath) => {
    return apiClient.post('/rename', null, {
        params: { oldPath, newPath },
    });
};

/**
 * Move um arquivo ou pasta de um local para outro.
 * @param {Object} fileOperation - Objeto contendo as informações para mover.
 * @param {String} fileOperation.fileName - Nome do arquivo/pasta a ser movido.
 * @param {String} fileOperation.from - Caminho atual do arquivo/pasta.
 * @param {String} fileOperation.to - Novo caminho para onde o arquivo/pasta será movido.
 * @returns {Promise<Object>} - Status e mensagem de sucesso ou erro.
 */
export const moveFile = ({ fileName, from, to }) => {
    return apiClient.post('/move', { fileName, from, to });
};

/**
 * Faz o download de um arquivo.
 * @param {String} path - Caminho do arquivo a ser baixado.
 * @returns {Promise<void>} - Redireciona o navegador para o download.
 */
export const downloadFile = (path) => {
    const fullPath = path.replace(/\/+/g, '/');
    window.location.href = `/api/file/download?path=${encodeURIComponent(fullPath)}`;
};

/**
 * Descompacta um arquivo ZIP.
 * @param {String} path - Caminho do arquivo ZIP a ser descompactado.
 * @returns {Promise<void>}
 */
export const unzipFile = (path) => {
    const fullPath = path.replace(/\/+/g, '/');
    return apiClient.post('/unzip', {}, { params: { path: fullPath } });
};

/**
 * Exclui um arquivo ou pasta.
 * @param {String} path - Caminho do arquivo/pasta a ser excluído.
 * @returns {Promise<void>}
 */
export const deleteItem = (path) => {
    const fullPath = path.replace(/\/+/g, '/');
    return apiClient.delete('/delete', { params: { path: fullPath } });
};
