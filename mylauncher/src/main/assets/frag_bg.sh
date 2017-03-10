precision mediump float;
varying  vec4 vColor; //接收从顶点着色器过来的参数
varying vec3 vPosition;//接收从顶点着色器过来的顶点位置
uniform float uAlpha;
void main() {
	vec4 bcolor =  vColor; 
	float temp = bcolor.a;
	bcolor.a = temp * uAlpha;
    gl_FragColor = bcolor;//给此片元颜色值
}