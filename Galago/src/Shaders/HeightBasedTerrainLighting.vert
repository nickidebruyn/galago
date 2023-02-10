uniform float m_tilingFactor;
uniform float m_terrainSize;

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat4 g_WorldMatrix;
uniform mat3 g_NormalMatrix;
uniform mat4 g_ViewMatrix;

uniform vec4 g_LightColor;
uniform vec4 g_LightPosition;
uniform vec4 g_AmbientLightColor;

attribute vec3 inNormal;
attribute vec3 inPosition;

varying vec3 hbNormal;
varying vec4 hbPosition;

varying vec3 vNormal;
varying vec3 vPosition;
varying vec3 vViewDir;
varying vec4 vLightDir;

varying vec3 lightVec;

varying vec4 AmbientSum;
varying vec4 DiffuseSum;
varying vec4 SpecularSum;

// JME3 lights in world space
void lightComputeDir(in vec3 worldPos, in vec4 color, in vec4 position, out vec4 lightDir) {
    float posLight = step(0.5, color.w);
    vec3 tempVec = position.xyz * sign(posLight - 0.5) - (worldPos * posLight);
    lightVec.xyz = tempVec;
    float dist = length(tempVec);
    lightDir.w = clamp(1.0 - position.w * dist * posLight, 0.0, 1.0);
    lightDir.xyz = tempVec / vec3(dist);
}

void main() {
    hbNormal = normalize(inNormal);
    hbPosition = g_WorldMatrix * vec4(inPosition, 0.0);

    vec4 pos = vec4(inPosition, 1.0);

    gl_Position = g_WorldViewProjectionMatrix * pos;

    vec3 wvPosition = (g_WorldViewMatrix * pos).xyz;
    vec3 wvNormal  = normalize(g_NormalMatrix * inNormal);
    vec3 viewDir = normalize(-wvPosition);

    vec4 wvLightPos = (g_ViewMatrix * vec4(g_LightPosition.xyz,clamp(g_LightColor.w,0.0,1.0)));
    wvLightPos.w = g_LightPosition.w;
    vec4 lightColor = g_LightColor;

    vNormal = wvNormal;

    vPosition = wvPosition;
    vViewDir = viewDir;

    lightComputeDir(wvPosition, lightColor, wvLightPos, vLightDir);

    AmbientSum  = vec4(0.2, 0.2, 0.2, 1.0) * g_AmbientLightColor; // Default: ambient color is dark gray
    DiffuseSum  = lightColor;
    SpecularSum = lightColor;
}
