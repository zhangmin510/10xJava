class Solution {
    public void reverseString(char[] s) {
        int j = 0;
        char c = 0;
        for (int n = s.length -1, i = (n - 1) >> 1; i >= 0; i--) {
            j = n - i;
            c = s[i];
            s[i] = s[j];
            s[j] = c;
        }
    }
}