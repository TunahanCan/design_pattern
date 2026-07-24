package com.can.behavirol.iterator;

import java.util.Objects;

/**
 * Başka bir iterator'ı şirket ölçütüyle süzen lazy iterator decorator'ı.
 */
public class CompanyProfileIterator implements ProfileIterator {

    private final ProfileIterator delegate;
    private final String company;
    private Profile bufferedProfile;

    public CompanyProfileIterator(ProfileIterator delegate, String company) {
        this.delegate = Objects.requireNonNull(delegate, "delegate cannot be null");
        if (company == null || company.isBlank()) {
            throw new IllegalArgumentException("company cannot be blank");
        }
        this.company = company.trim();
    }

    @Override
    public boolean hasMore() {
        if (bufferedProfile != null) {
            return true;
        }

        while (delegate.hasMore()) {
            Profile candidate = delegate.getNext();
            if (candidate != null && company.equalsIgnoreCase(candidate.company())) {
                bufferedProfile = candidate;
                return true;
            }
        }
        return false;
    }

    @Override
    public Profile getNext() {
        if (!hasMore()) {
            return null;
        }

        Profile result = bufferedProfile;
        bufferedProfile = null;
        return result;
    }
}
