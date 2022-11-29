uniform vec4 g_LightDirection;

uniform vec3 m_region1;
uniform vec3 m_region2;
uniform vec3 m_region3;
uniform vec3 m_region4;

uniform sampler2D m_DiffuseMap;
uniform sampler2D m_DiffuseMap_1;
uniform sampler2D m_DiffuseMap_2;
uniform sampler2D m_DiffuseMap_3;
uniform sampler2D m_SlopeDiffuseMap;

uniform float m_slopeTileFactor;
uniform float m_terrainSize;

varying vec3 hbNormal;
varying vec4 hbPosition;

varying vec4 AmbientSum;
varying vec4 DiffuseSum;
varying vec4 SpecularSum;

varying vec3 vNormal;
varying vec3 vPosition;
varying vec3 vViewDir;
varying vec4 vLightDir;
varying vec3 lightVec;

vec4 GenerateTerrainColor() {
    float height = hbPosition.y;
    vec4 p = hbPosition / m_terrainSize;

    vec3 blend = abs( hbNormal );
    blend = (blend -0.2) * 0.7;
    blend = normalize(max(blend, 0.00001));      // Force weights to sum to 1.0 (very important!)
    float b = (blend.x + blend.y + blend.z);
    blend /= vec3(b, b, b);

    vec4 terrainColor = vec4(0.0, 0.0, 0.0, 1.0);

    float m_regionMin = 0.0;
    float m_regionMax = 0.0;
    float m_regionRange = 0.0;
    float m_regionWeight = 0.0;

    vec4 slopeCol1 = texture2D(m_SlopeDiffuseMap, p.yz * m_slopeTileFactor);
    vec4 slopeCol2 = texture2D(m_SlopeDiffuseMap, p.xy * m_slopeTileFactor);

    // Terrain m_region 1.
    m_regionMin = m_region1.x;
    m_regionMax = m_region1.y;
    m_regionRange = m_regionMax - m_regionMin;
    m_regionWeight = (m_regionRange - abs(height - m_regionMax)) / m_regionRange;
    m_regionWeight = max(0.0, m_regionWeight);
    terrainColor += m_regionWeight * texture2D(m_DiffuseMap, p.xz * m_region1.z);

    // Terrain m_region 2.
    m_regionMin = m_region2.x;
    m_regionMax = m_region2.y;
    m_regionRange = m_regionMax - m_regionMin;
    m_regionWeight = (m_regionRange - abs(height - m_regionMax)) / m_regionRange;
    m_regionWeight = max(0.0, m_regionWeight);
    terrainColor += m_regionWeight * (texture2D(m_DiffuseMap_1, p.xz * m_region2.z));

    // Terrain m_region 3.
    m_regionMin = m_region3.x;
    m_regionMax = m_region3.y;
    m_regionRange = m_regionMax - m_regionMin;
    m_regionWeight = (m_regionRange - abs(height - m_regionMax)) / m_regionRange;
    m_regionWeight = max(0.0, m_regionWeight);
    terrainColor += m_regionWeight * texture2D(m_DiffuseMap_2, p.xz * m_region3.z);

    // Terrain m_region 4.
    m_regionMin = m_region4.x;
    m_regionMax = m_region4.y;
    m_regionRange = m_regionMax - m_regionMin;
    m_regionWeight = (m_regionRange - abs(height - m_regionMax)) / m_regionRange;
    m_regionWeight = max(0.0, m_regionWeight);
    terrainColor += m_regionWeight * texture2D(m_DiffuseMap_3, p.xz * m_region4.z);

    return (blend.y * terrainColor + blend.x * slopeCol1 + blend.z * slopeCol2);
}

float tangDot(in vec3 v1, in vec3 v2) {
    float d = dot(v1,v2);
    return d;
}

float lightComputeDiffuse(in vec3 norm, in vec3 lightdir, in vec3 viewdir) {
    return max(0.0, dot(norm, lightdir));
}

float lightComputeSpecular(in vec3 norm, in vec3 viewdir, in vec3 lightdir, in float shiny) {
    if (shiny <= 1.0) {
        return 0.0;
    }

    vec3 R = reflect(-lightdir, norm);
    return pow(max(tangDot(R, viewdir), 0.0), shiny);
}

vec2 computeLighting(in vec3 wvPos, in vec3 wvNorm, in vec3 wvViewDir, in vec3 wvLightDir) {
    float shininess = 0.0;

    float diffuseFactor = lightComputeDiffuse(wvNorm, wvLightDir, wvViewDir);
    float specularFactor = lightComputeSpecular(wvNorm, wvViewDir, wvLightDir, shininess);
    specularFactor *= step(1.0, shininess);

    float att = vLightDir.w;

    return vec2(diffuseFactor, specularFactor) * vec2(att);
}

void main() {
    vec4 diffuseColor = GenerateTerrainColor();
    float spotFallOff = 1.0;

    if(g_LightDirection.w != 0.0) {
        vec3 L = normalize(lightVec.xyz);
        vec3 spotdir = normalize(g_LightDirection.xyz);
        float curAngleCos = dot(-L, spotdir);
        float innerAngleCos = floor(g_LightDirection.w) * 0.001;
        float outerAngleCos = fract(g_LightDirection.w);
        float innerMinusOuter = innerAngleCos - outerAngleCos;

        spotFallOff = (curAngleCos - outerAngleCos) / innerMinusOuter;

        if(spotFallOff <= 0.0) {
            gl_FragColor = AmbientSum * diffuseColor;
            return;
        } else {
            spotFallOff = clamp(spotFallOff, 0.0, 1.0);
        }
    }

    vec3 normal = vNormal;
    vec4 lightDir = vLightDir;
    lightDir.xyz = normalize(lightDir.xyz);
    vec2 light = computeLighting(vPosition, normal, vViewDir.xyz, lightDir.xyz)*spotFallOff;
    vec4 specularColor = vec4(1.0);

    gl_FragColor =  AmbientSum * diffuseColor +
                    DiffuseSum * diffuseColor  * light.x +
                    SpecularSum * specularColor * light.y;
}
