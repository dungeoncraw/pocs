if __name__ == '__main__':
    dial_codes = [
        (86, 'China'),
        (91, 'India'),
        (1, 'United States'),
        (62, 'Indonesia'),
        (55, 'Brazil'),
        (92, 'Pakistan'),
        (880, 'Bangladesh'),
        (234, 'Nigeria'),
        (7, 'Russia'),
        (81, 'Japan'),
        (880, 'Bangladesh'),
        (1, 'United States')
    ]
    country_dial = {country: code for code, country in dial_codes}
    print(country_dial)
    filtered_country = {code: country.upper() for country, code in sorted(country_dial.items()) if code < 70}
    print(filtered_country)