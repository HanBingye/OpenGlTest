uniform mat4 u_Matrix;
attribute vec3 a_Position;
varying vec3 v_Position;

void main()
{
    v_Position = a_Position;
    // 反转z分量，使得我们可以把世界的右手坐标空间转换为天空盒所期望的左手坐标空间
    v_Position.z = -v_Position.z;

    gl_Position = u_Matrix * vec4(a_Position, 1.0);
    /**
    它确保天空盒的每一部分都将位于归一化设备坐标的远平面上以及场景中的其他一切后面。
    这个技巧能够奏效，是因为透视除法把一切都除以w,并且w除以它自己，结果等于1。透视除法之后，z最终就在值为1的远平面上了。
    */
    gl_Position = gl_Position.xyww;
}    
