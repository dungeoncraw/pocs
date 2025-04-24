pub fn miri_test() {
    unsafe {
        let mut data = [0; 10];
        // Slice entire array
        let slice1_all = &mut data[..];
        // Pointer for the entire array using as_mut_ptr
        let ptr2_all = slice1_all.as_mut_ptr();

        // pointer
        let ptr3_at_0 = ptr2_all;
        // pointer
        let ptr4_at_1 = ptr2_all.add(1);
        // reference
        let ref5_at_0 = &mut *ptr3_at_0;
        // reference
        let ref6_at_1 = &mut *ptr4_at_1;

        *ref6_at_1 += 6;
        *ref5_at_0 += 5;
        *ptr4_at_1 += 4;
        *ptr3_at_0 += 3;

        for idx in 0..10 {
            *ptr2_all.add(idx) += idx;
        }

        // Should be [8, 12, 4, 6, 8, 10, 12, 14, 16, 18]
        println!("{:?}", &data[..]);
    }
}
