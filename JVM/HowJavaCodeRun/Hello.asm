Loaded disassembler from hsdis-amd64.dylib
Decoding compiled method 0x0000000117f3e450:
Code:
[Disassembling for mach='i386:x86-64']
[Entry Point]
[Constants]
  # {method} {0x0000000112276fc8} 'hashCode' '()I' in 'java/lang/String'
  #           [sp+0x40]  (sp of caller)
  0x0000000117f3e5c0: mov    0x8(%rsi),%r10d
  0x0000000117f3e5c4: shl    $0x3,%r10
  0x0000000117f3e5c8: cmp    %rax,%r10
  0x0000000117f3e5cb: jne    0x0000000117e84e20  ;   {runtime_call}
  0x0000000117f3e5d1: data32 data32 nopw 0x0(%rax,%rax,1)
  0x0000000117f3e5dc: data32 data32 xchg %ax,%ax
[Verified Entry Point]
  0x0000000117f3e5e0: mov    %eax,-0x14000(%rsp)
  0x0000000117f3e5e7: push   %rbp
  0x0000000117f3e5e8: sub    $0x30,%rsp
  0x0000000117f3e5ec: movabs $0x112434de0,%rax  ;   {metadata(method data for {method} {0x0000000112276fc8} 'hashCode' '()I' in 'java/lang/String')}
  0x0000000117f3e5f6: mov    0xdc(%rax),%edi
  0x0000000117f3e5fc: add    $0x8,%edi
  0x0000000117f3e5ff: mov    %edi,0xdc(%rax)
  0x0000000117f3e605: movabs $0x112276fc8,%rax  ;   {metadata({method} {0x0000000112276fc8} 'hashCode' '()I' in 'java/lang/String')}
  0x0000000117f3e60f: and    $0x1ff8,%edi
  0x0000000117f3e615: cmp    $0x0,%edi
  0x0000000117f3e618: je     0x0000000117f3e751  ;*aload_0
                                                ; - java.lang.String::hashCode@0 (line 1454)

  0x0000000117f3e61e: mov    0x10(%rsi),%eax    ;*getfield hash
                                                ; - java.lang.String::hashCode@1 (line 1454)

  0x0000000117f3e621: cmp    $0x0,%eax
  0x0000000117f3e624: movabs $0x112434de0,%rdi  ;   {metadata(method data for {method} {0x0000000112276fc8} 'hashCode' '()I' in 'java/lang/String')}
  0x0000000117f3e62e: movabs $0x108,%rbx
  0x0000000117f3e638: jne    0x0000000117f3e648
  0x0000000117f3e63e: movabs $0x118,%rbx
  0x0000000117f3e648: mov    (%rdi,%rbx,1),%rdx
  0x0000000117f3e64c: lea    0x1(%rdx),%rdx
  0x0000000117f3e650: mov    %rdx,(%rdi,%rbx,1)
  0x0000000117f3e654: jne    0x0000000117f3e745  ;*ifne
                                                ; - java.lang.String::hashCode@6 (line 1455)

  0x0000000117f3e65a: mov    0xc(%rsi),%edi
  0x0000000117f3e65d: shl    $0x3,%rdi          ;*getfield value
                                                ; - java.lang.String::hashCode@10 (line 1455)

  0x0000000117f3e661: mov    0xc(%rdi),%ebx     ;*arraylength
                                                ; - java.lang.String::hashCode@13 (line 1455)
                                                ; implicit exception: dispatches to 0x0000000117f3e768
  0x0000000117f3e664: cmp    $0x0,%ebx
  0x0000000117f3e667: movabs $0x112434de0,%rdx  ;   {metadata(method data for {method} {0x0000000112276fc8} 'hashCode' '()I' in 'java/lang/String')}
  0x0000000117f3e671: movabs $0x128,%rcx
  0x0000000117f3e67b: jle    0x0000000117f3e68b
  0x0000000117f3e681: movabs $0x138,%rcx
  0x0000000117f3e68b: mov    (%rdx,%rcx,1),%r8
  0x0000000117f3e68f: lea    0x1(%r8),%r8
  0x0000000117f3e693: mov    %r8,(%rdx,%rcx,1)
  0x0000000117f3e697: jle    0x0000000117f3e745  ;*ifle
                                                ; - java.lang.String::hashCode@14 (line 1455)

  0x0000000117f3e69d: mov    $0x0,%edx
  0x0000000117f3e6a2: jmpq   0x0000000117f3e70a  ;*iload_3
                                                ; - java.lang.String::hashCode@24 (line 1458)

  0x0000000117f3e6a7: nop
  0x0000000117f3e6a8: movslq %edx,%rcx
  0x0000000117f3e6ab: movzwl 0x10(%rdi,%rcx,2),%ecx  ;*caload
                                                ; - java.lang.String::hashCode@39 (line 1459)

  0x0000000117f3e6b0: mov    %rax,%r8
  0x0000000117f3e6b3: shl    $0x5,%eax
  0x0000000117f3e6b6: sub    %r8d,%eax
  0x0000000117f3e6b9: add    %ecx,%eax
  0x0000000117f3e6bb: inc    %edx
  0x0000000117f3e6bd: movabs $0x112434de0,%rcx  ;   {metadata(method data for {method} {0x0000000112276fc8} 'hashCode' '()I' in 'java/lang/String')}
  0x0000000117f3e6c7: mov    0xe0(%rcx),%r8d
  0x0000000117f3e6ce: add    $0x8,%r8d
  0x0000000117f3e6d2: mov    %r8d,0xe0(%rcx)
  0x0000000117f3e6d9: movabs $0x112276fc8,%rcx  ;   {metadata({method} {0x0000000112276fc8} 'hashCode' '()I' in 'java/lang/String')}
  0x0000000117f3e6e3: and    $0xfff8,%r8d
  0x0000000117f3e6ea: cmp    $0x0,%r8d
  0x0000000117f3e6ee: je     0x0000000117f3e76d  ; OopMap{rdi=Oop rsi=Oop off=308}
                                                ;*goto
                                                ; - java.lang.String::hashCode@45 (line 1458)

  0x0000000117f3e6f4: test   %eax,-0xc3615fa(%rip)        # 0x000000010bbdd100
                                                ;   {poll}
  0x0000000117f3e6fa: movabs $0x112434de0,%rcx  ;   {metadata(method data for {method} {0x0000000112276fc8} 'hashCode' '()I' in 'java/lang/String')}
  0x0000000117f3e704: incl   0x168(%rcx)        ;*goto
                                                ; - java.lang.String::hashCode@45 (line 1458)

  0x0000000117f3e70a: cmp    %ebx,%edx
  0x0000000117f3e70c: movabs $0x112434de0,%rcx  ;   {metadata(method data for {method} {0x0000000112276fc8} 'hashCode' '()I' in 'java/lang/String')}
  0x0000000117f3e716: movabs $0x148,%r8
  0x0000000117f3e720: jge    0x0000000117f3e730
  0x0000000117f3e726: movabs $0x158,%r8
  0x0000000117f3e730: mov    (%rcx,%r8,1),%r9
  0x0000000117f3e734: lea    0x1(%r9),%r9
  0x0000000117f3e738: mov    %r9,(%rcx,%r8,1)
  0x0000000117f3e73c: jl     0x0000000117f3e6a8  ;*if_icmpge
                                                ; - java.lang.String::hashCode@30 (line 1458)

  0x0000000117f3e742: mov    %eax,0x10(%rsi)    ;*putfield hash
                                                ; - java.lang.String::hashCode@50 (line 1461)

  0x0000000117f3e745: add    $0x30,%rsp
  0x0000000117f3e749: pop    %rbp
  0x0000000117f3e74a: test   %eax,-0xc361650(%rip)        # 0x000000010bbdd100
                                                ;   {poll_return}
  0x0000000117f3e750: retq   
  0x0000000117f3e751: mov    %rax,0x8(%rsp)
  0x0000000117f3e756: movq   $0xffffffffffffffff,(%rsp)
  0x0000000117f3e75e: callq  0x0000000117f3cbe0  ; OopMap{rsi=Oop off=419}
                                                ;*synchronization entry
                                                ; - java.lang.String::hashCode@-1 (line 1454)
                                                ;   {runtime_call}
  0x0000000117f3e763: jmpq   0x0000000117f3e61e
  0x0000000117f3e768: callq  0x0000000117f38680  ; OopMap{rsi=Oop rdi=Oop off=429}
                                                ;*arraylength
                                                ; - java.lang.String::hashCode@13 (line 1455)
                                                ;   {runtime_call}
  0x0000000117f3e76d: mov    %rcx,0x8(%rsp)
  0x0000000117f3e772: movq   $0x2d,(%rsp)
  0x0000000117f3e77a: callq  0x0000000117f3cbe0  ; OopMap{rdi=Oop rsi=Oop off=447}
                                                ;*goto
                                                ; - java.lang.String::hashCode@45 (line 1458)
                                                ;   {runtime_call}
  0x0000000117f3e77f: jmpq   0x0000000117f3e6f4
  0x0000000117f3e784: nop
  0x0000000117f3e785: nop
  0x0000000117f3e786: mov    0x2a8(%r15),%rax
  0x0000000117f3e78d: movabs $0x0,%r10
  0x0000000117f3e797: mov    %r10,0x2a8(%r15)
  0x0000000117f3e79e: movabs $0x0,%r10
  0x0000000117f3e7a8: mov    %r10,0x2b0(%r15)
  0x0000000117f3e7af: add    $0x30,%rsp
  0x0000000117f3e7b3: pop    %rbp
  0x0000000117f3e7b4: jmpq   0x0000000117eab0e0  ;   {runtime_call}
  0x0000000117f3e7b9: hlt    
  0x0000000117f3e7ba: hlt    
  0x0000000117f3e7bb: hlt    
  0x0000000117f3e7bc: hlt    
  0x0000000117f3e7bd: hlt    
  0x0000000117f3e7be: hlt    
  0x0000000117f3e7bf: hlt    
[Exception Handler]
[Stub Code]
  0x0000000117f3e7c0: callq  0x0000000117f3a520  ;   {no_reloc}
  0x0000000117f3e7c5: mov    %rsp,-0x28(%rsp)
  0x0000000117f3e7ca: sub    $0x80,%rsp
  0x0000000117f3e7d1: mov    %rax,0x78(%rsp)
  0x0000000117f3e7d6: mov    %rcx,0x70(%rsp)
  0x0000000117f3e7db: mov    %rdx,0x68(%rsp)
  0x0000000117f3e7e0: mov    %rbx,0x60(%rsp)
  0x0000000117f3e7e5: mov    %rbp,0x50(%rsp)
  0x0000000117f3e7ea: mov    %rsi,0x48(%rsp)
  0x0000000117f3e7ef: mov    %rdi,0x40(%rsp)
  0x0000000117f3e7f4: mov    %r8,0x38(%rsp)
  0x0000000117f3e7f9: mov    %r9,0x30(%rsp)
  0x0000000117f3e7fe: mov    %r10,0x28(%rsp)
  0x0000000117f3e803: mov    %r11,0x20(%rsp)
  0x0000000117f3e808: mov    %r12,0x18(%rsp)
  0x0000000117f3e80d: mov    %r13,0x10(%rsp)
  0x0000000117f3e812: mov    %r14,0x8(%rsp)
  0x0000000117f3e817: mov    %r15,(%rsp)
  0x0000000117f3e81b: movabs $0x10c9b52ca,%rdi  ;   {external_word}
  0x0000000117f3e825: movabs $0x117f3e7c5,%rsi  ;   {internal_word}
  0x0000000117f3e82f: mov    %rsp,%rdx
  0x0000000117f3e832: and    $0xfffffffffffffff0,%rsp
  0x0000000117f3e836: callq  0x000000010c7e93fc  ;   {runtime_call}
  0x0000000117f3e83b: hlt    
[Deopt Handler Code]
  0x0000000117f3e83c: movabs $0x117f3e83c,%r10  ;   {section_word}
  0x0000000117f3e846: push   %r10
  0x0000000117f3e848: jmpq   0x0000000117e863c0  ;   {runtime_call}
  0x0000000117f3e84d: hlt    
  0x0000000117f3e84e: hlt    
  0x0000000117f3e84f: hlt    
