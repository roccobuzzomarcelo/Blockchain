#include <iostream>
#include <iomanip>
#include <cstring>
#include <chrono>
#include <cuda_runtime.h>

// Tipos
typedef unsigned char BYTE;
typedef unsigned int  WORD;

// ---------- ESTRUCTURA CONTEXTO MD5 ----------
typedef struct {
    BYTE data[64];
    WORD datalen;
    unsigned long long bitlen;
    WORD state[4];
} CUDA_MD5_CTX;

// ---------- MACROS MD5 ----------
#ifndef ROTLEFT
#define ROTLEFT(a,b) ((a << b) | (a >> (32-b)))
#endif

#define F(x,y,z) ((x & y) | (~x & z))
#define G(x,y,z) ((x & z) | (y & ~z))
#define H(x,y,z) (x ^ y ^ z)
#define I(x,y,z) (y ^ (x | ~z))

#define FF(a,b,c,d,m,s,t) { a += F(b,c,d) + m + t; a = b + ROTLEFT(a,s); }
#define GG(a,b,c,d,m,s,t) { a += G(b,c,d) + m + t; a = b + ROTLEFT(a,s); }
#define HH(a,b,c,d,m,s,t) { a += H(b,c,d) + m + t; a = b + ROTLEFT(a,s); }
#define II(a,b,c,d,m,s,t) { a += I(b,c,d) + m + t; a = b + ROTLEFT(a,s); }

// ---------- FUNCIONES MD5 (device) ----------
__device__ void cuda_md5_transform(CUDA_MD5_CTX *ctx, const BYTE data[]) {
    WORD a, b, c, d, m[16], i, j;

    for (i = 0, j = 0; i < 16; ++i, j += 4)
        m[i] = (data[j]) + (data[j + 1] << 8) + (data[j + 2] << 16) + (data[j + 3] << 24);

    a = ctx->state[0]; b = ctx->state[1]; c = ctx->state[2]; d = ctx->state[3];

    FF(a,b,c,d,m[0],7,0xd76aa478);  FF(d,a,b,c,m[1],12,0xe8c7b756); FF(c,d,a,b,m[2],17,0x242070db); FF(b,c,d,a,m[3],22,0xc1bdceee);
    FF(a,b,c,d,m[4],7,0xf57c0faf);  FF(d,a,b,c,m[5],12,0x4787c62a); FF(c,d,a,b,m[6],17,0xa8304613); FF(b,c,d,a,m[7],22,0xfd469501);
    FF(a,b,c,d,m[8],7,0x698098d8);  FF(d,a,b,c,m[9],12,0x8b44f7af); FF(c,d,a,b,m[10],17,0xffff5bb1);FF(b,c,d,a,m[11],22,0x895cd7be);
    FF(a,b,c,d,m[12],7,0x6b901122); FF(d,a,b,c,m[13],12,0xfd987193);FF(c,d,a,b,m[14],17,0xa679438e);FF(b,c,d,a,m[15],22,0x49b40821);

    GG(a,b,c,d,m[1],5,0xf61e2562);  GG(d,a,b,c,m[6],9,0xc040b340);  GG(c,d,a,b,m[11],14,0x265e5a51); GG(b,c,d,a,m[0],20,0xe9b6c7aa);
    GG(a,b,c,d,m[5],5,0xd62f105d);  GG(d,a,b,c,m[10],9,0x02441453);GG(c,d,a,b,m[15],14,0xd8a1e681);GG(b,c,d,a,m[4],20,0xe7d3fbc8);
    GG(a,b,c,d,m[9],5,0x21e1cde6);  GG(d,a,b,c,m[14],9,0xc33707d6);GG(c,d,a,b,m[3],14,0xf4d50d87); GG(b,c,d,a,m[8],20,0x455a14ed);
    GG(a,b,c,d,m[13],5,0xa9e3e905); GG(d,a,b,c,m[2],9,0xfcefa3f8);  GG(c,d,a,b,m[7],14,0x676f02d9); GG(b,c,d,a,m[12],20,0x8d2a4c8a);

    HH(a,b,c,d,m[5],4,0xfffa3942);  HH(d,a,b,c,m[8],11,0x8771f681);HH(c,d,a,b,m[11],16,0x6d9d6122);HH(b,c,d,a,m[14],23,0xfde5380c);
    HH(a,b,c,d,m[1],4,0xa4beea44);  HH(d,a,b,c,m[4],11,0x4bdecfa9);HH(c,d,a,b,m[7],16,0xf6bb4b60);HH(b,c,d,a,m[10],23,0xbebfbc70);
    HH(a,b,c,d,m[13],4,0x289b7ec6); HH(d,a,b,c,m[0],11,0xeaa127fa);HH(c,d,a,b,m[3],16,0xd4ef3085);HH(b,c,d,a,m[6],23,0x04881d05);
    HH(a,b,c,d,m[9],4,0xd9d4d039);  HH(d,a,b,c,m[12],11,0xe6db99e5);HH(c,d,a,b,m[15],16,0x1fa27cf8);HH(b,c,d,a,m[2],23,0xc4ac5665);

    II(a,b,c,d,m[0],6,0xf4292244);  II(d,a,b,c,m[7],10,0x432aff97); II(c,d,a,b,m[14],15,0xab9423a7);II(b,c,d,a,m[5],21,0xfc93a039);
    II(a,b,c,d,m[12],6,0x655b59c3); II(d,a,b,c,m[3],10,0x8f0ccc92); II(c,d,a,b,m[10],15,0xffeff47d);II(b,c,d,a,m[1],21,0x85845dd1);
    II(a,b,c,d,m[8],6,0x6fa87e4f);  II(d,a,b,c,m[15],10,0xfe2ce6e0);II(c,d,a,b,m[6],15,0xa3014314); II(b,c,d,a,m[13],21,0x4e0811a1);
    II(a,b,c,d,m[4],6,0xf7537e82);  II(d,a,b,c,m[11],10,0xbd3af235);II(c,d,a,b,m[2],15,0x2ad7d2bb); II(b,c,d,a,m[9],21,0xeb86d391);

    ctx->state[0] += a;
    ctx->state[1] += b;
    ctx->state[2] += c;
    ctx->state[3] += d;
}

__device__ void cuda_md5_init(CUDA_MD5_CTX *ctx) {
    ctx->datalen = 0;
    ctx->bitlen = 0;
    ctx->state[0] = 0x67452301;
    ctx->state[1] = 0xEFCDAB89;
    ctx->state[2] = 0x98BADCFE;
    ctx->state[3] = 0x10325476;
}

__device__ void cuda_md5_update(CUDA_MD5_CTX *ctx, const BYTE data[], size_t len) {
    size_t i;
    for (i = 0; i < len; ++i) {
        ctx->data[ctx->datalen++] = data[i];
        if (ctx->datalen == 64) {
            cuda_md5_transform(ctx, ctx->data);
            ctx->bitlen += 512;
            ctx->datalen = 0;
        }
    }
}

__device__ void cuda_md5_final(CUDA_MD5_CTX *ctx, BYTE hash[]) {
    size_t i = ctx->datalen;
    if (ctx->datalen < 56) {
        ctx->data[i++] = 0x80;
        while (i < 56) ctx->data[i++] = 0x00;
    } else {
        ctx->data[i++] = 0x80;
        while (i < 64) ctx->data[i++] = 0x00;
        cuda_md5_transform(ctx, ctx->data);
        memset(ctx->data, 0, 56);
    }

    ctx->bitlen += ctx->datalen * 8;
    for (int j = 0; j < 8; ++j)
        ctx->data[56 + j] = (ctx->bitlen >> (8 * j)) & 0xFF;

    cuda_md5_transform(ctx, ctx->data);
    for (i = 0; i < 4; ++i) {
        hash[i]      = (ctx->state[0] >> (8 * i)) & 0xFF;
        hash[i + 4]  = (ctx->state[1] >> (8 * i)) & 0xFF;
        hash[i + 8]  = (ctx->state[2] >> (8 * i)) & 0xFF;
        hash[i + 12] = (ctx->state[3] >> (8 * i)) & 0xFF;
    }
}

// ---------- FUNCIONES AUXILIARES ----------
__device__ int construir_mensaje(char* out, const char* base, int base_len, long nonce) {
    for (int i = 0; i < base_len; ++i) out[i] = base[i];
    int i = base_len, pos = 0;
    char buffer[20];
    long temp = nonce;
    do {
        buffer[pos++] = '0' + (temp % 10);
        temp /= 10;
    } while (temp > 0);
    for (int j = pos - 1; j >= 0; --j) out[i++] = buffer[j];
    return i;
}

__device__ bool starts_with(BYTE* hash, int hash_len, const char* prefix, int prefix_len) {
    for (int i = 0; i < prefix_len; ++i) {
        char c = prefix[i];
        char h = ((hash[i / 2] >> ((1 - (i % 2)) * 4)) & 0xF);
        h = (h < 10) ? ('0' + h) : ('a' + (h - 10));
        if (c != h) return false;
    }
    return true;
}

// ---------- KERNEL ----------
__global__ void cuda_md5_miner_kernel(
    const char* base, int base_len,
    const char* prefix, int prefix_len,
    long start_nonce, BYTE* resultado_hash,
    long* resultado_nonce, int* encontrado,
    long cantidad_total)
{
    long idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx >= cantidad_total) return;
    long nonce = start_nonce + idx;

    char mensaje[128];
    int mensaje_len = construir_mensaje(mensaje, base, base_len, nonce);

    CUDA_MD5_CTX ctx;
    BYTE hash[16];

    cuda_md5_init(&ctx);
    cuda_md5_update(&ctx, (BYTE*)mensaje, mensaje_len);
    cuda_md5_final(&ctx, hash);

    if (starts_with(hash, 16, prefix, prefix_len)) {
        if (atomicCAS(encontrado, 0, 1) == 0) {
            *resultado_nonce = nonce;
            for (int i = 0; i < 16; ++i) resultado_hash[i] = hash[i];
        }
    }
}

// ---------- HOST ----------
struct ResultadoMinado {
    std::string hash;
    long nonce;
};

ResultadoMinado* minarGPU(const std::string& prefijo, const std::string& cadena, long minNonce, long maxNonce) {
    long cantidad_nonce = maxNonce - minNonce + 1;

    char* d_base; char* d_prefijo; BYTE* d_hash_result;
    long* d_nonce_result; int* d_encontrado;

    cudaMalloc(&d_base, cadena.size());
    cudaMemcpy(d_base, cadena.c_str(), cadena.size(), cudaMemcpyHostToDevice);

    cudaMalloc(&d_prefijo, prefijo.size());
    cudaMemcpy(d_prefijo, prefijo.c_str(), prefijo.size(), cudaMemcpyHostToDevice);

    cudaMalloc(&d_hash_result, 16);
    cudaMalloc(&d_nonce_result, sizeof(long));
    cudaMalloc(&d_encontrado, sizeof(int));
    cudaMemset(d_encontrado, 0, sizeof(int));

    int threads = 256;
    int blocks = (cantidad_nonce + threads - 1) / threads;

    auto inicio = std::chrono::high_resolution_clock::now();

    cuda_md5_miner_kernel<<<blocks, threads>>>(
        d_base, cadena.size(), d_prefijo, prefijo.size(),
        minNonce, d_hash_result, d_nonce_result, d_encontrado, cantidad_nonce
    );
    cudaDeviceSynchronize();

    auto fin = std::chrono::high_resolution_clock::now();
    auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(fin - inicio).count();

    int encontrado = 0;
    cudaMemcpy(&encontrado, d_encontrado, sizeof(int), cudaMemcpyDeviceToHost);

    if (encontrado) {
        long nonce_encontrado;
        BYTE hash_bytes[16];
        cudaMemcpy(&nonce_encontrado, d_nonce_result, sizeof(long), cudaMemcpyDeviceToHost);
        cudaMemcpy(hash_bytes, d_hash_result, 16, cudaMemcpyDeviceToHost);

        std::stringstream ss;
        for (int i = 0; i < 16; ++i)
            ss << std::hex << std::setw(2) << std::setfill('0') << (int)hash_bytes[i];

        std::cout << "Tiempo: " << ms << " ms\n";
        cudaFree(d_base); cudaFree(d_prefijo); cudaFree(d_hash_result); cudaFree(d_nonce_result); cudaFree(d_encontrado);
        return new ResultadoMinado{ ss.str(), nonce_encontrado };
    }

    std::cout << "Tiempo: " << ms << " ms\nNo se encontró un hash válido\n";
    cudaFree(d_base); cudaFree(d_prefijo); cudaFree(d_hash_result); cudaFree(d_nonce_result); cudaFree(d_encontrado);
    return nullptr;
}

int main() {
    std::string cadena = "bloque123|TxABC";
    std::string prefijo = "0000";
    long minNonce = 100000;
    long maxNonce = 200000;

    ResultadoMinado* resultado = minarGPU(prefijo, cadena, minNonce, maxNonce);
    if (resultado) {
        std::cout << "Nonce: " << resultado->nonce << "\nHash : " << resultado->hash << std::endl;
        delete resultado;
    }

    return 0;
}
