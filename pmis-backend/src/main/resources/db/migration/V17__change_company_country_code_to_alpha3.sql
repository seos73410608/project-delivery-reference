-- V17: Change company country code to ISO 3166-1 alpha-3
-- Existing ISO alpha-2 country codes are converted to alpha-3.

-- ---------------------------------------------------------------------------
-- 1. Remove the existing default and check constraint
-- ---------------------------------------------------------------------------

ALTER TABLE public.companies
    ALTER COLUMN country_code DROP DEFAULT;

ALTER TABLE public.companies
    DROP CONSTRAINT ck_companies_country_code;


-- ---------------------------------------------------------------------------
-- 2. Convert existing country codes
-- ---------------------------------------------------------------------------

UPDATE public.companies
SET country_code = CASE country_code
    WHEN 'KR' THEN 'KOR'
    WHEN 'US' THEN 'USA'
    WHEN 'JP' THEN 'JPN'
    WHEN 'CN' THEN 'CHN'
    WHEN 'GB' THEN 'GBR'
    WHEN 'CA' THEN 'CAN'
    WHEN 'MX' THEN 'MEX'
    WHEN 'DE' THEN 'DEU'
    WHEN 'FR' THEN 'FRA'
    WHEN 'IT' THEN 'ITA'
    WHEN 'ES' THEN 'ESP'
    WHEN 'AU' THEN 'AUS'
    WHEN 'NZ' THEN 'NZL'
    WHEN 'SG' THEN 'SGP'
    WHEN 'HK' THEN 'HKG'
    WHEN 'TW' THEN 'TWN'
    WHEN 'IN' THEN 'IND'
    WHEN 'BR' THEN 'BRA'
    WHEN 'RU' THEN 'RUS'
    WHEN 'CH' THEN 'CHE'
    WHEN 'NL' THEN 'NLD'
    WHEN 'SE' THEN 'SWE'
    WHEN 'NO' THEN 'NOR'
    WHEN 'DK' THEN 'DNK'
    WHEN 'FI' THEN 'FIN'
    WHEN 'IE' THEN 'IRL'
    WHEN 'BE' THEN 'BEL'
    WHEN 'AT' THEN 'AUT'
    WHEN 'PL' THEN 'POL'
    WHEN 'TH' THEN 'THA'
    WHEN 'VN' THEN 'VNM'
    WHEN 'ID' THEN 'IDN'
    WHEN 'MY' THEN 'MYS'
    WHEN 'PH' THEN 'PHL'
    WHEN 'AE' THEN 'ARE'
    WHEN 'SA' THEN 'SAU'
    WHEN 'ZA' THEN 'ZAF'
    WHEN 'TR' THEN 'TUR'
    WHEN 'AR' THEN 'ARG'
    WHEN 'CL' THEN 'CHL'
    WHEN 'CO' THEN 'COL'
    WHEN 'PE' THEN 'PER'
    WHEN 'PT' THEN 'PRT'
    WHEN 'GR' THEN 'GRC'
    WHEN 'CZ' THEN 'CZE'
    WHEN 'HU' THEN 'HUN'
    WHEN 'RO' THEN 'ROU'
    WHEN 'UA' THEN 'UKR'
    WHEN 'IL' THEN 'ISR'
    WHEN 'EG' THEN 'EGY'
    ELSE country_code
END;


-- ---------------------------------------------------------------------------
-- 3. Change column length and default value
-- ---------------------------------------------------------------------------

ALTER TABLE public.companies
    ALTER COLUMN country_code TYPE character varying(3);

ALTER TABLE public.companies
    ALTER COLUMN country_code SET DEFAULT 'KOR';


-- ---------------------------------------------------------------------------
-- 4. Apply ISO alpha-3 format constraint
-- ---------------------------------------------------------------------------

ALTER TABLE public.companies
    ADD CONSTRAINT ck_companies_country_code
    CHECK (country_code ~ '^[A-Z]{3}$');