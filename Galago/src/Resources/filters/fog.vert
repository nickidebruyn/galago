uniform mat4 g_WorldViewProjectionMatrix;
uniform vec3 g_CameraPosition;
uniform mat4 g_WorldMatrix;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute float inSize;

const float SIZE_MULTIPLIER = 800.0;

varying vec2 texCoord;
varying float dist;

void main() {

    vec4 pos = vec4(inPosition, 1.0);
    gl_Position = g_WorldViewProjectionMatrix * pos;
    texCoord = inTexCoord;

    vec4 worldPos = g_WorldMatrix * pos;
    dist = distance(g_CameraPosition.xyz, worldPos.xyz);
    gl_PointSize = max(1.0, (inSize * SIZE_MULTIPLIER ) / dist);

}