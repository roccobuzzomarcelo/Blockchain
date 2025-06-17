#include <iostream>
#include <iomanip>
#include <cstring>

extern "C" {
    #include "md5.cuh"
}

constexpr size_t MD5_HASH_SIZE = 16; // tamaño fijo de MD5

int main(int argc, char** argv) {
    if (argc < 2) {
        std::cerr << "Uso: " << argv[0] << " \"texto_a_hashear\"" << std::endl;
        return 1;
    }

    const char* mensaje = argv[1];
    size_t mensaje_len = std::strlen(mensaje);

    BYTE output[MD5_HASH_SIZE] = {0};

    mcm_cuda_md5_hash_batch((BYTE*)mensaje, (WORD)mensaje_len, output, 1);

    std::cout << "El string original es: " << mensaje << std::endl;
    std::cout << "El hash MD5 calculado es: ";
    for (size_t i = 0; i < MD5_HASH_SIZE; ++i) {
        std::cout << std::hex << std::setw(2) << std::setfill('0') << (int)output[i];
    }
    std::cout << std::dec << std::endl;

    return 0;
}
