class Solution1 {
    public void reverseString(char[] s) {
        for (int n = s.length -1, i = (n - 1) >> 1; i >= 0; i--) {
            int j = n - i;
            char c = s[i];
            s[i] = s[j];
            s[j] = c;
        }
    }
}